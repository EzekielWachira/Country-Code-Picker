# Phone numbers

The phone layer sits on top of the country picker and Google
[libphonenumber](https://github.com/google/libphonenumber). It gives you a unified phone number
field, country-aware validation and formatting, and an optional verification flow you back with your
own provider.

| Page | What it covers |
|---|---|
| [Phone number field](field.md) | `PhoneNumberField`, `PhoneNumberFieldState`, `PhoneNumberValue`, input styles and sizes, the standalone dial-code selector |
| [Validation and formatting](validation.md) | `PhoneNumberValidity`, `PhoneNumberValidator`, `PhoneNumberFormatter`, parsing pasted numbers |
| [Phone verification](verification.md) | `PhoneNumberVerificationHandler`, `PhoneVerificationController`, `PhoneVerificationState` |

## The one rule

Never build an E.164 string by concatenating a dial code onto typed digits. That is wrong for every
country with a national trunk prefix: a UK mobile typed as `07400 123456` is `+447400123456`, not
`+4407400123456`. `PhoneNumberValue.e164Number` is produced by libphonenumber and is `null` while the
number cannot be parsed. Read it; do not rebuild it.

```kotlin
val phoneState = rememberPhoneNumberFieldState(initialCountry = kenya)

PhoneNumberField(
    state = phoneState,
    onValueChange = { value: PhoneNumberValue ->
        value.e164Number           // "+254712345678", or null while unparseable
        value.internationalNumber  // "+254 712 345 678"
        value.isValid              // true only for a real, dialable number
    },
)
```
