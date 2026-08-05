/**
 * Copyright (c) 2025 Ezekiel Wachira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ezzy.ccp.countrypicker.phone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.model.UiText

/**
 * Sequences [PhoneNumberVerificationHandler] calls and exposes the result as a single observable
 * [PhoneVerificationState].
 *
 * This is where the rules that are easy to get wrong live:
 * - only a number with [PhoneNumberValue.isValid] can start verification;
 * - a request in flight blocks a second one, so double-tapping "Send code" sends one SMS;
 * - resend is refused until the handler's cooldown elapses;
 * - **editing the number invalidates any verification already obtained** — see [onPhoneNumberChanged].
 *
 * The controller is UI-framework-agnostic apart from holding its state in a Compose
 * `mutableStateOf`; it contains no composables and can be driven from a ViewModel just as easily.
 * Every method is `suspend` and does its work on the caller's dispatcher — nothing here blocks the
 * main thread, and no coroutine is launched internally, so cancellation is the caller's to control.
 */
@Stable
class PhoneVerificationController(
    private val handler: PhoneNumberVerificationHandler,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) {

    /** The current verification state. Observe this from composition. */
    var state: PhoneVerificationState by mutableStateOf(PhoneVerificationState.Idle)
        private set

    /**
     * The number the active flow belongs to, so a later edit can be detected even when the
     * verification succeeded and the state no longer carries the field value.
     */
    private var subjectE164: String? = null

    /**
     * Starts verification for [phoneNumber].
     *
     * No-ops when a request is already in flight, or when [phoneNumber] is not valid — in the latter
     * case the state becomes [PhoneVerificationState.Failed] with an actionable message rather than
     * silently doing nothing.
     */
    suspend fun requestCode(phoneNumber: PhoneNumberValue) {
        if (state.isInProgress) return

        val e164 = phoneNumber.e164Number
        if (!phoneNumber.isValid || e164 == null) {
            state = PhoneVerificationState.Failed(
                message = UiText.resource(R.string.ccp_verify_requires_valid_number),
                retryable = false,
            )
            return
        }

        subjectE164 = e164
        state = PhoneVerificationState.SendingCode
        val result = runCatchingRequest { handler.requestVerification(phoneNumber) }
        applyRequestResult(result)
    }

    /**
     * Submits [code] against the active challenge.
     *
     * No-ops when there is no challenge or one is already being checked, so a double tap on "Verify"
     * cannot consume two attempts against a rate-limited endpoint.
     */
    suspend fun submitCode(code: String) {
        val verificationId = state.activeVerificationId ?: return
        if (state is PhoneVerificationState.Verifying) return

        state = PhoneVerificationState.Verifying(verificationId)
        val result = try {
            handler.verifyCode(verificationId, code)
        } catch (cancellation: kotlinx.coroutines.CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            VerificationResult.Failure(UiText.resource(R.string.ccp_verify_failed))
        }

        state = when (result) {
            is VerificationResult.Verified -> PhoneVerificationState.Verified(result.e164Number)
            is VerificationResult.Invalid -> PhoneVerificationState.Failed(
                message = result.message ?: UiText.resource(R.string.ccp_verify_failed),
                verificationId = verificationId,
            )

            VerificationResult.Expired -> PhoneVerificationState.Expired
            is VerificationResult.Failure -> PhoneVerificationState.Failed(
                message = result.message,
                verificationId = verificationId.takeIf { result.retryable },
                retryable = result.retryable,
            )
        }
    }

    /**
     * Re-sends the code for the active challenge.
     *
     * Refused while a call is in flight or before [resendAvailableInSeconds] reaches zero, so the
     * cooldown the handler asked for is actually honoured rather than merely displayed.
     */
    suspend fun resendCode() {
        val verificationId = state.activeVerificationId ?: return
        if (state.isInProgress || resendAvailableInSeconds() > 0) return

        state = PhoneVerificationState.SendingCode
        val result = runCatchingRequest { handler.resendCode(verificationId) }
        applyRequestResult(result)
    }

    /**
     * Tells the controller the field's number changed.
     *
     * A verified state is only meaningful for the exact number that was verified, so any change to
     * the E.164 value resets the flow to [PhoneVerificationState.Idle]. Without this, a user could
     * verify one number, edit a digit, and submit a form that still claims to be verified.
     *
     * Idempotent: passing the same number repeatedly (as happens on every recomposition-driven
     * callback) does nothing.
     */
    fun onPhoneNumberChanged(phoneNumber: PhoneNumberValue) {
        val next = phoneNumber.e164Number
        if (next == subjectE164) return
        subjectE164 = next
        if (state != PhoneVerificationState.Idle) state = PhoneVerificationState.Idle
    }

    /** Clears all verification state, e.g. when a form is reset. */
    fun reset() {
        subjectE164 = null
        state = PhoneVerificationState.Idle
    }

    /**
     * Seconds remaining before a resend is allowed; `0` when it is allowed now.
     *
     * Read this from a ticking composable (`LaunchedEffect` + 1s delay) to render a countdown —
     * the controller intentionally does not run its own timer, so it never keeps a coroutine alive
     * after the UI is gone.
     */
    fun resendAvailableInSeconds(): Int {
        val availableAt = (state as? PhoneVerificationState.CodeSent)?.resendAvailableAtMillis
            ?: return 0
        val remaining = availableAt - currentTimeMillis()
        return if (remaining <= 0) 0 else ((remaining + 999) / 1000).toInt()
    }

    /** True when [phoneNumber] is the number that was successfully verified. */
    fun isVerified(phoneNumber: PhoneNumberValue): Boolean {
        val verified = (state as? PhoneVerificationState.Verified)?.e164Number ?: return false
        return verified == phoneNumber.e164Number
    }

    private suspend fun runCatchingRequest(
        block: suspend () -> VerificationRequestResult,
    ): VerificationRequestResult = try {
        block()
    } catch (cancellation: kotlinx.coroutines.CancellationException) {
        throw cancellation
    } catch (_: Exception) {
        VerificationRequestResult.Failure(UiText.resource(R.string.ccp_verify_request_failed))
    }

    private fun applyRequestResult(result: VerificationRequestResult) {
        state = when (result) {
            is VerificationRequestResult.Success -> PhoneVerificationState.CodeSent(
                verificationId = result.verificationId,
                destination = result.destination,
                resendAvailableAtMillis = result.resendAvailableAtMillis,
            )

            is VerificationRequestResult.Failure -> PhoneVerificationState.Failed(
                message = result.message,
                retryable = result.retryable,
            )
        }
    }
}

/**
 * Remembers a [PhoneVerificationController] for [handler].
 *
 * Keyed on [handler] so swapping the handler starts a clean flow rather than leaving a challenge id
 * from the previous one in place.
 */
@Composable
fun rememberPhoneVerificationController(
    handler: PhoneNumberVerificationHandler,
): PhoneVerificationController = remember(handler) { PhoneVerificationController(handler) }
