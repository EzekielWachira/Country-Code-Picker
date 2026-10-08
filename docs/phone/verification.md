# Phone verification

Verification is optional and fully decoupled. The library ships **no** verification backend, on
purpose: a bundled fake that accepts `123456` is the kind of thing that reaches production. You
implement one interface against your own SMS, Firebase Auth or backend provider; the library sequences
the calls, renders the state, and enforces the rules that are easy to get wrong.

## 1. Implement the handler

```kotlin
class MyVerificationHandler(private val api: MyAuthApi) : PhoneNumberVerificationHandler {

    override suspend fun requestVerification(phoneNumber: PhoneNumberValue): VerificationRequestResult =
        try {
            val challenge = api.sendCode(phoneNumber.e164Number!!)
            VerificationRequestResult.Success(
                verificationId = challenge.id,
                destination = phoneNumber.internationalNumber,
                resendAvailableAtMillis = System.currentTimeMillis() + 30_000,
            )
        } catch (e: IOException) {
            VerificationRequestResult.Failure(UiText.resource(Res.string.network_error), retryable = true)
        }

    override suspend fun verifyCode(verificationId: String, code: String): VerificationResult =
        when (api.checkCode(verificationId, code)) {
            CheckResult.Ok -> VerificationResult.Verified(api.numberFor(verificationId))
            CheckResult.WrongCode -> VerificationResult.Invalid()
            CheckResult.Expired -> VerificationResult.Expired
        }

    override suspend fun resendCode(verificationId: String): VerificationRequestResult =
        requestVerificationAgain(verificationId)
}
```

Nothing in the interface touches Compose, so it can live in a data module and be unit-tested without
Android. The library guarantees `phoneNumber.isValid` is `true` before calling
`requestVerification`. Throwing is acceptable (the controller surfaces `Failed`), but returning a
`Failure` with a message is better.

| Result type | Cases |
|---|---|
| `VerificationRequestResult` | `Success(verificationId, destination, resendAvailableAtMillis?)`, `Failure(message, retryable)` |
| `VerificationResult` | `Verified(e164Number)`, `Invalid(message?)`, `Expired`, `Failure(message, retryable)` |

`resendAvailableAtMillis` is epoch millis rather than `java.time.Instant` so the library stays usable
at `minSdk 24` without core-library desugaring.

## 2. Create a controller and attach it to the field

```kotlin
val verification = rememberPhoneVerificationController(MyVerificationHandler(api))

PhoneNumberField(
    state = phoneState,
    onValueChange = {},
    verificationController = verification,
)
```

With a controller attached the field shows a tick once the *current* number is verified, and tells
the controller whenever the number changes.

## 3. Drive the flow

```kotlin
val scope = rememberCoroutineScope()

Button(
    enabled = phoneState.value.isValid && !verification.state.isInProgress,
    onClick = { scope.launch { verification.requestCode(phoneState.value) } },
) { Text("Send code") }

when (val state = verification.state) {
    is PhoneVerificationState.CodeSent -> {
        Text("Code sent to ${state.destination}")
        OtpEntry(onSubmit = { code -> scope.launch { verification.submitCode(code) } })
        ResendButton(controller = verification)
    }
    is PhoneVerificationState.Verifying -> CircularProgressIndicator()
    is PhoneVerificationState.Verified -> Text("Verified: ${state.e164Number}")
    is PhoneVerificationState.Failed -> Text(state.message.resolve(), color = MaterialTheme.colorScheme.error)
    PhoneVerificationState.Expired -> Text("Code expired. Request a new one.")
    PhoneVerificationState.Idle, PhoneVerificationState.SendingCode -> Unit
}
```

### Resend with a countdown

The controller intentionally runs no timer of its own, so it never keeps a coroutine alive after the
UI is gone. Tick from a composable:

```kotlin
@Composable
fun ResendButton(controller: PhoneVerificationController) {
    var secondsLeft by remember { mutableIntStateOf(controller.resendAvailableInSeconds()) }
    LaunchedEffect(controller.state) {
        while (true) {
            secondsLeft = controller.resendAvailableInSeconds()
            if (secondsLeft == 0) break
            delay(1_000)
        }
    }
    val scope = rememberCoroutineScope()
    TextButton(
        enabled = secondsLeft == 0,
        onClick = { scope.launch { controller.resendCode() } },
    ) { Text(if (secondsLeft == 0) "Resend code" else "Resend in ${secondsLeft}s") }
}
```

## States

```kotlin
sealed interface PhoneVerificationState {
    data object Idle
    data object SendingCode
    data class CodeSent(val verificationId: String, val destination: String, val resendAvailableAtMillis: Long?)
    data class Verifying(val verificationId: String)
    data class Verified(val e164Number: String)
    data class Failed(val message: UiText, val verificationId: String?, val retryable: Boolean)
    data object Expired

    val isInProgress: Boolean          // SendingCode or Verifying
    val activeVerificationId: String?  // the challenge id, if any
}
```

This is the whole observable surface of verification. A host with its own OTP screen can drive it
from these states and ignore the library's UI entirely.

## Rules the controller enforces

- Only a number with `isValid == true` can start verification. Calling `requestCode` with an invalid
  number produces `Failed` with an actionable message rather than silently doing nothing.
- A request in flight blocks a second one, so double-tapping "Send code" sends one SMS and
  double-tapping "Verify" consumes one attempt against a rate-limited endpoint.
- Resend is refused until the handler's cooldown elapses, so the cooldown is honoured rather than
  merely displayed.
- **Editing the number invalidates any verification already obtained.** Any change to the E.164 value
  resets the flow to `Idle`. A form can never submit "verified" for a number the user has since
  changed. `isVerified(value)` checks the controller against a specific number for the same reason.
- Every method is `suspend` and runs on the caller's dispatcher; nothing is launched internally, so
  cancellation is yours to control. The controller can be driven from a ViewModel as easily as from a
  composable.

`reset()` clears everything, for example when a form is reset.
