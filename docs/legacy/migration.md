# Migrating from `PhoneNumberInput`

You do not have to migrate. `PhoneNumberInput` is fully supported and already runs on the Country
Picker foundation, so it benefits from the bigger dataset, ranked search and progressive formatting
with no change on your part.

Move to the newer API when you want things `PhoneNumberInput` does not expose: multi-selection,
region filters, recents and suggestions, phone verification, the unified outlined field, or the
theming system.

## Building blocks

| Old | New |
|---|---|
| `PhoneNumberInput(...)` | `PhoneNumberField(state = rememberPhoneNumberFieldState(...), ...)` |
| `PhoneState` | `PhoneNumberFieldState` (`country`, `nationalDigits`, `value: PhoneNumberValue`, `selectCountry`, `clear`) |
| `Phone` | `PhoneNumberValue` (`e164Number`, `internationalNumber`, `isValid`, `validity`) |
| `Country` (`com.ezzy.ccp.model`) | `Country` (`com.ezzy.ccp.countrypicker.model`): adds ISO alpha-3, region and aliases |
| `countriesToShow` / `countriesExclude` | `CountryPickerConfig.allowedCountryCodes` / `excludedCountryCodes` |
| `pinnedCountries` | `CountryPickerConfig.suggestedCountryCodes` |
| `setCountry = "KE"` | `rememberPhoneNumberFieldState(initialCountry = DefaultCountryDataSource.findByIso2("KE")!!)` |
| `value = "+254…"` | `rememberPhoneNumberFieldState(initialNumber = "+254…")` or `state.setFullNumber(...)` |
| `CCPConfig.autoDetectCountry` | `detector = rememberDefaultCountryDetector()` on a selector, or run the detector and call `state.selectCountry` |
| `CCPConfig.enforceMaxLength` | `rememberPhoneNumberFieldState(enforceMaxLength = ...)` |
| `CCPConfig.showClearButton` | `PhoneNumberField(showClearButton = ...)` |
| `CCPConfig.showLabel` / `showPhonePrefixDivider` / `phoneFieldSize` | `PhoneNumberInputStyle.labelMode` / `showPrefixDivider` / `size` |
| `phoneHint` / `label` (`String`) | `placeholder` / `label` (`UiText`) |
| `isError` / `errorMessage` | `isError` / `errorMessage: UiText?` (combined with, not replacing, local validation) |
| `onDone` | `onDone` (unchanged semantics: only when valid) |
| `CCPColors`, `CCPConfig` shape params | `CountryPickerColors` / `CountryPickerShapes` via `CountryPickerDefaults` |
| `CountryPickerStyle.Dropdown` | No direct equivalent; the new API always uses the bottom sheet |
| `CountryListStyle.Grid` | No direct equivalent; the new sheet is a grouped list |
| — (did not exist) | `CountrySelector`, `MultiCountrySelector`, region filters, `RecentCountryStore`, `PhoneNumberVerificationHandler` |

## Side by side

=== "Before"

    ```kotlin
    val phoneState = rememberPhoneState()

    PhoneNumberInput(
        state = phoneState,
        setCountry = "KE",
        countriesToShow = listOf("KE", "UG", "TZ"),
        onValueChange = { phone -> onPhone(phone.phoneNumber, phone.isValid) },
    )
    ```

=== "After"

    ```kotlin
    val phoneState = rememberPhoneNumberFieldState(
        initialCountry = DefaultCountryDataSource.findByIso2("KE")!!,
    )

    PhoneNumberField(
        state = phoneState,
        config = CountryPickerDefaults.phoneConfig(allowedCountryCodes = setOf("KE", "UG", "TZ")),
        onValueChange = { value -> onPhone(value.e164Number, value.isValid) },
    )
    ```

Conversions between the two models are public if you need to bridge during a gradual migration; see
[Converting to and from the legacy model](../country-picker/data.md#converting-to-and-from-the-legacy-model).

## Behaviour changes in the shared foundation

A few behaviours changed for **both** APIs when `PhoneNumberInput` moved onto the new foundation.
They are also described in the doc comments on `countryList` and `PhoneState`.

- **Bigger dataset, current names.** The bundled list grew from about 200 to 236 entries, and a few
  names follow current ISO usage: `"Czech Republic"` → `"Czechia"`, `"Turkey"` → `"Türkiye"`,
  `"Swaziland"` → `"Eswatini"`. Former names remain searchable as aliases.
- **NANP dial codes lost their hyphens.** `"+1-809"` → `"+1809"`. The hyphenated form was not valid
  and broke E.164 normalization.
- **Progressive formatting.** The field groups digits as you type. Previously it stayed ungrouped
  until the number parsed cleanly.
- **Honest E.164.** `unformattedPhone` / `Phone.phoneNumber` is empty rather than echoing raw input
  while a number is unparseable. Callers that persisted the value could previously store something
  that was not E.164 at all.
- **Granular validity.** `PhoneState.validity` and `PhoneState.value` are new; `isValid` behaves
  exactly as before.
