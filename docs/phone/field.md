# Phone number field

`PhoneNumberField` is an international phone number field: the country prefix (flag, dial code,
chevron) and the number in **one** field, with a label that rests where the number goes and lifts
above it as soon as the field has focus or a value.

```kotlin
val kenya = remember { DefaultCountryDataSource.findByIso2("KE")!! }
val phoneState = rememberPhoneNumberFieldState(initialCountry = kenya)

PhoneNumberField(
    state = phoneState,
    onValueChange = { value -> viewModel.onPhoneChanged(value) },
    label = UiText.of("Mobile number"),
)
```

What you get:

- **Progressive formatting.** Digits group as the user types (`71` → `712` → `712 345 678`) using
  libphonenumber's `AsYouTypeFormatter`, the same engine the platform dialer uses.
- **A complete value on every change.** `onValueChange` delivers a
  [`PhoneNumberValue`](#phonenumbervalue) with formatted forms, `e164Number` and validity, so
  submission code never concatenates a dial code onto digits.
- **Country changes preserve the number.** Switching country keeps the typed digits, re-formats them
  for the new region and **re-validates**. A number valid in one region is never carried over as still
  valid in another.
- **Quiet while typing.** Errors are withheld until the field has been touched (blurred or submitted),
  and an incomplete number is never flagged as invalid.
- **Country-aware helper text.** *"Formats live for Kenya · e.g. +254 712 345 678"*, generated from
  libphonenumber metadata for the selected country, so no example number is hardcoded anywhere.
- **Max length enforcement.** Input is capped at the region's maximum (with tolerance for countries
  whose national lengths vary), so a mistyped extra digit cannot silently invalidate a correct number.
- **Ghost digits.** The empty field shows the country's example number; once typing starts, the rest
  of it is ghosted as zeros — `712 3|00 000` — so the expected length and grouping stay visible.
- **Progress and confirmation.** A hairline along the bottom fills as digits arrive and turns green
  when the number is valid, and a valid number earns a badge naming its kind — `✓ Mobile`,
  `✓ Landline` — so a landline entered where a mobile is wanted is visible before submitting. Screen
  readers hear the same as the field's state: *"Valid number, Mobile"*.
- **Tools while editing, results at rest.** The clear button appears only while the field has focus,
  as on iOS, so a filled field shows the number and its badge rather than its controls.

!!! warning "Pass an initial country"
    The default `state` starts on the first dataset entry alphabetically (Afghanistan). Always pass
    `rememberPhoneNumberFieldState(initialCountry = …)`, ideally combined with
    [country detection](../country-picker/detection.md).

## Parameters

| Parameter | Default | Purpose |
|---|---|---|
| `onValueChange` | required | Called whenever the number or country changes |
| `state` | see warning above | Hoist it to read the number, change the country or clear the field from outside |
| `enabled` / `readOnly` | `true` / `false` | Standard text-field semantics; both also gate the prefix |
| `label` | "Phone number" | Floating label. Rendered only when `inputStyle.labelMode` is `Floating` |
| `accessibilityLabel` | `label` | The label exposed to screen readers regardless of whether the visible label renders |
| `placeholder` | `null` | Placeholder inside the editor |
| `variant` | `Elevated` | `Elevated`, `Outlined`, `Filled`, `Underlined` or `Card` — the same containers as the [country selector](../country-picker/selector.md#variants) |
| `inputStyle` | `PhoneNumberInputDefaults.style()` | Label, prefix content, divider, size, and the ghost digits, progress, badge and check. See [Styling the field](#styling-the-field) |
| `config` | `CountryPickerDefaults.phoneConfig()` | The embedded picker's [configuration](../country-picker/configuration.md): allowed/excluded countries, suggestions, search |
| `showHelperText` | `true` | The live "Formats live for …" helper |
| `showClearButton` | `true` | A clear button while the field has content |
| `autofillEnabled` | `true` | Advertise the field to Android Autofill and password managers. See [Autofill](#autofill) |
| `isError` / `errorMessage` | `false` / `null` | Force the error treatment and override the derived message, e.g. for a server-side rejection. Combined with local validation, never replacing it |
| `validateWhileTyping` | `false` | Show validation errors before the field loses focus |
| `verificationController` | `null` | Opt in to [verification](verification.md) |
| `recentCountryStore`, `repository` | no-op / bundled | As on every selector |
| `style`, `flagContent` | the theme's | [Theming](../theming.md) |
| `onDone` | `{}` | Invoked when the keyboard action fires **and** the number is valid |

## `PhoneNumberFieldState`

The digits are the single source of truth; the formatted text is derived from them and never stored
independently, which is what keeps the display and the E.164 output from drifting apart.

```kotlin
val state = rememberPhoneNumberFieldState(
    initialCountry = kenya,
    initialNumber = "+254712345678",   // optional; an E.164 value also sets the country
    enforceMaxLength = true,
    allowedNumberTypes = emptySet(),   // or PhoneNumberType.SmsCapable to require a mobile
)
```

| Member | Purpose |
|---|---|
| `country` | The selected country |
| `nationalDigits` | Raw digits, no separators |
| `value: PhoneNumberValue` | The complete evaluated value |
| `hasBeenTouched` | True once the user left the field or attempted submission. Errors are withheld until then |
| `selectCountry(country)` | Changes the country, keeps the digits, re-formats and re-validates |
| `setFullNumber(raw, allowCountryChange = true)` | Parses a pasted or prefilled number. `+4915123456789` switches the field to Germany; a bare `0712345678` carries no country signal and leaves the country alone |
| `clear()` | Clears the number, leaves the country alone |
| `markTouched()` | Makes validation messages visible, e.g. on a submit tap |
| `onTextChanged(TextFieldValue)` | The field's own text callback. Strips everything non-digit so pasting `(0712) 345-678` works, and adopts the country from text that names one — see [Autofill](#autofill) |
| `allowedNumberTypes` | The line types the field accepts. See [Restricting the line type](validation.md#restricting-the-line-type) |

The state is saved with `rememberSaveable` (country code, digits and touched flag), so it survives
rotation and process death.

## `PhoneNumberValue`

```kotlin
@Immutable
data class PhoneNumberValue(
    val country: Country,
    val nationalNumber: String,            // "712345678"
    val formattedNationalNumber: String,   // "0712 345 678"
    val internationalNumber: String,       // "+254 712 345 678" — always safe to display
    val e164Number: String?,               // "+254712345678", or null when unparseable
    val isPossible: Boolean,               // length is plausible for the region
    val isValid: Boolean,                  // a real, dialable number — the only flag that should gate submission
    val validity: PhoneNumberValidity,     // the granular reason behind isValid
    val numberType: PhoneNumberType?,      // mobile, landline, toll-free… null until parseable
) {
    val isEmpty: Boolean
}
```

`e164Number` is `null`, deliberately not an empty string, whenever the number is not yet parseable.
Never treat `null` as "no number"; check `isEmpty` or `nationalNumber` for that. `isPossible` turns
true before `isValid` does, which makes it the right signal for "keep typing" affordances. The
`validity` values are described in [Validation and formatting](validation.md).

## Styling the field

`inputStyle` is a `PhoneNumberInputStyle`, built with `PhoneNumberInputDefaults.style(…)` or
`PhoneNumberInputDefaults.hiddenLabelStyle(…)`.

```kotlin
PhoneNumberField(
    state = phoneState,
    onValueChange = {},
    inputStyle = PhoneNumberInputDefaults.style(
        labelMode = InputLabelMode.Hidden,
        prefixContentMode = PhonePrefixContentMode.CountryCodeAndDialCode,   // "KE +254"
        showPrefixDivider = false,
        size = PhoneFieldSize.Compact,
    ),
)
```

| Property | Default | Options |
|---|---|---|
| `labelMode` | `Floating` | `Floating` or `Hidden`. Hiding never removes the label from accessibility |
| `showDropdownIcon` | `true` | The chevron in the prefix |
| `showPrefixDivider` | `true` | The thin vertical divider between prefix and editor. Off removes it from the layout entirely, leaving no dead space |
| `prefixContentMode` | `FlagAndDialCode` | `FlagOnly` (🇰🇪), `DialCodeOnly` (+254), `FlagAndDialCode` (🇰🇪 +254), `CountryCodeAndDialCode` (KE +254) |
| `size` | `Regular` | `Regular`, `Compact`, `ExtraCompact` |
| `showGhostDigits` | `true` | Ghost the rest of the example number as the user types |
| `showProgress` | `true` | The hairline that fills as digits arrive |
| `showNumberType` | `true` | The `✓ Mobile` badge on a valid number |
| `showValidIndicator` | `true` | A check on a valid number when the badge is off |

The prefix's flag follows the style's [`flagStyle` and `flagSource`](../theming.md#flags), like every
other flag in the library.

### Sizes

`PhoneFieldSize.Compact` and `ExtraCompact` scale padding, flag, chevron, icon-button and font sizes
**together**, so a shorter field looks proportioned rather than squeezed.

| `PhoneFieldSize` | Content scale | Font scale |
|---|---|---|
| `Regular` | 1.0 | 1.0 |
| `Compact` | 0.8 | 0.92 |
| `ExtraCompact` | 0.65 | 0.85 |

!!! note
    The compact sizes deliberately let the prefix's touch target drop below the usual 48dp floor,
    because the field itself is shorter than that. Use them where the surrounding layout justifies it.

## Standalone dial-code selector

`PhoneCountryCodeSelector` is the `🇰🇪 +254 ˅` pill on its own, for a host composing its own phone
row. It opens the identical sheet with `phoneConfig()` applied.

```kotlin
Row {
    PhoneCountryCodeSelector(
        selectedCountry = phoneState.country,
        onCountrySelected = phoneState::selectCountry,   // re-formats and re-validates
    )
    OutlinedTextField(
        value = phoneState.textFieldValue,
        onValueChange = phoneState::onTextChanged,
        modifier = Modifier.weight(1f),
    )
}
```

Inside `PhoneNumberField` the prefix is *not* this pill: it is a bare clickable region sharing the
field's single outline, which is what avoids the "two separate rounded boxes" look. The standalone
pill remains correct on its own.

## Autofill

The field declares `ContentType.PhoneNumber + ContentType.PhoneNumberNational`, so Android Autofill
and password managers offer to fill it. Nothing to wire up — it is on by default; pass
`autofillEnabled = false` to opt out.

Both content types are declared because providers store different shapes: some hold a full E.164
number, others the national part with the country code separately. Either works, because the fill
arrives through the same path as a paste and that path understands international numbers:

```
autofill supplies "+254712345678"
  → PhoneNumberFieldState.onTextChanged
  → text names a country, so the field switches to Kenya
  → national digits become 712345678
```

Without that, the `+` would be stripped, `254` would be read as the first three digits of the
subscriber number, and the field would report a plausible but wrong value under whichever country
happened to be selected. The same applies to a user *pasting* or typing `+254…` by hand, which is
why the behaviour lives in the state rather than in an autofill-specific callback.

The rule for adopting a country is deliberately narrow:

| Input | Country changes? |
|---|---|
| `+254712345678` typed or pasted | Yes — the `+` form always names its country |
| `00254712345678` pasted | Yes |
| `00…` typed one key at a time | No — `00` is two ordinary keystrokes, and re-parsing mid-entry would yank the country out from under the user |
| `0712345678` | No — a bare national number carries no country signal |
| `+2` (incomplete) | No — not until the input actually resolves to a country |

## Keyboard "Done"

`onDone` fires only when the IME action is triggered **and** `value.isValid` is true, so wiring it to
form submission is safe:

```kotlin
PhoneNumberField(
    state = phoneState,
    onValueChange = {},
    onDone = { submit(phoneState.value.e164Number!!) },
)
```
