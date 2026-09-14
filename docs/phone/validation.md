# Validation and formatting

Both live in `com.ezzy.ccp.countrypicker.phone` and are pure, stateless objects over libphonenumber.
Every function is cheap enough to call on each keystroke on the main thread.

## Validity

Validation is explicitly **not** a length comparison. libphonenumber knows which number ranges are
assigned in each region, so `+254 000 000 000` has a plausible length and is still rejected. The
result is a `PhoneNumberValidity` rather than a boolean so "still typing" never looks like "wrong".

| `PhoneNumberValidity` | Meaning | `isError` |
|---|---|---|
| `Empty` | Nothing typed. Show nothing | no |
| `Incomplete` | Plausible prefix, not yet long enough. The normal state while typing | no |
| `Valid` | A real, dialable number. **The only value for which submission should be allowed** | no |
| `TooShort` | Shorter than any valid number for the region and cannot grow into one | yes |
| `TooLong` | Longer than any valid number for the region | yes |
| `Possible` | Length is plausible but the number is not an assigned one | yes |
| `Invalid` | Parsed, but not valid for the region and not merely a length problem | yes |
| `InvalidCountryCode` | The leading calling code matches no region | yes |
| `UnsupportedCountry` | libphonenumber has no metadata for the selected country. A configuration problem, not user error | yes |

```kotlin
when (phoneState.value.validity) {
    PhoneNumberValidity.Empty,
    PhoneNumberValidity.Incomplete -> Unit          // say nothing
    PhoneNumberValidity.Valid -> submit()
    else -> showError(PhoneNumberValidator.errorMessage(phoneState.value))
}
```

`PhoneNumberField` already shows and hides its own error text this way. Reach for
`PhoneNumberValidator` directly only when you need the result before the user submits, or outside a
Compose field.

## `PhoneNumberValidator`

| Function | Returns |
|---|---|
| `evaluate(nationalNumber, country)` | The complete `PhoneNumberValue`: formatted forms, E.164 and validity in one pass |
| `isValid(nationalNumber, country)` | Convenience predicate for "is this submittable" |
| `errorMessage(value, treatIncompleteAsError = false)` | The `UiText` to show under the field, or `null` for `Empty`, `Valid` and `Incomplete`. Pass `treatIncompleteAsError = true` when validating on submit |
| `helperText(country)` | *"Formats live for Kenya · e.g. +254 712 345 678"*, from the region's metadata |

## `PhoneNumberFormatter`

There is no hand-written digit-grouping table. Per-country grouping, national trunk prefixes and
example numbers all come from libphonenumber metadata, so adding a country never requires touching
formatting code.

| Function | Example | Notes |
|---|---|---|
| `formatAsYouType(digits, country)` | `"0712 345 678"` | National grouping, progressive mid-typing |
| `formatAsYouTypeInternational(digits, country)` | `"712 345 678"` | The grouping used next to a separately displayed dial code (no NANP parentheses, no trunk `0`) |
| `toE164(nationalNumber, country)` | `"+254712345678"` | `null` when unparseable |
| `toInternational(nationalNumber, country)` | `"+254 712 345 678"` | `null` when unparseable |
| `toNational(nationalNumber, country)` | `"0712 345 678"` | `null` when unparseable |
| `parse(nationalNumber, country)` | `Phonenumber.PhoneNumber?` | The raw libphonenumber object, `null` instead of throwing |
| `exampleNationalNumber(country)` | `"712 345 678"` | Drives the helper text |
| `expectedNationalDigits(country)` | `9` | From the example number; 15 when the region has no metadata |
| `maxNationalDigits(country)` | `expected + tolerance`, capped at 15 | Used to cap input without making variable-length numbers untypeable |

### Parsing pasted numbers

`parseInternational(raw, fallbackCountry)` splits a pasted or prefilled string into the country it
belongs to and its national digits, handling the three shapes users actually paste:

- full international, `"+254712345678"`: country resolved from the calling code;
- international with separators, `"+254 712 345 678"`: separators ignored;
- bare national, `"0712345678"`: no country information, so `fallbackCountry` is kept.

When the calling code is shared (`+1`, `+44`), libphonenumber's own region inference decides which
country from the number itself. The result's `countryWasDetected` flag tells you whether the input
carried a country signal, so an explicit user choice is never overwritten by a bare national number.
`PhoneNumberFieldState.setFullNumber` is built on this.
