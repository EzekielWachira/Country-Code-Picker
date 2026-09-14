# `PhoneNumberInput`

The original phone-number component. Its public API is unchanged; the implementation now runs on the
Country Picker foundation.

## Basic usage

```kotlin
PhoneNumberInput(
    onValueChange = { phone ->
        phone.formattedPhone  // "+254 724 154 958"
        phone.phoneNumber     // "+254724154958" (E.164; empty while unparseable)
        phone.country         // SelectedCountry(name, code, dialCode, flag)
        phone.isValid         // true / false
    },
)
```

## State hoisting

Hoist `PhoneState` when you need to read state or drive the field from outside the composable, for
example to clear it after submission or to validate on a button tap.

```kotlin
val phoneState = rememberPhoneState()

PhoneNumberInput(state = phoneState, onValueChange = { phone -> /* … */ })

Button(onClick = {
    if (phoneState.isValid) submit(phoneState.toPhone()) else phoneState.clearPhone()
}) { Text("Submit") }
```

## Picker style: bottom sheet or dropdown

```kotlin
// Default: modal bottom sheet
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(countryPickerStyle = CountryPickerStyle.BottomSheet),
)

// Compact inline dropdown (tablets, landscape, dense forms)
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(countryPickerStyle = CountryPickerStyle.Dropdown),
)
```

In the `BottomSheet` style the field is a real Material outlined text field with the flag, dial code
and chevron as leading content and a subtle divider before the number editor. The floating label and
the divider are independently togglable with `showLabel` (off by default) and `showPhonePrefixDivider`
(on by default). The `Dropdown` style keeps its original compact selector button with a popup anchored
below it.

## Error state

```kotlin
var isError by remember { mutableStateOf(false) }

PhoneNumberInput(
    isError = isError,
    errorMessage = "Please enter a valid phone number",
    onValueChange = { phone -> isError = !phone.isValid && phone.phoneNumber.isNotEmpty() },
)
```

## Pre-fill and country override

```kotlin
PhoneNumberInput(
    value = "+254724154958",   // pre-fill in E.164; the country is taken from it
    setCountry = "KE",         // or force a country by ISO code or name ("Kenya")
)
```

## Country filtering

```kotlin
PhoneNumberInput(
    countriesToShow = listOf("US", "GB", "KE", "FR", "AU"),   // allow list
    countriesExclude = listOf("KP", "IR"),                    // exclusions win
    pinnedCountries = listOf("US", "GB", "KE"),               // "Suggested" section at the top
)
```

## Behaviour options

```kotlin
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(
        showClearButton = true,        // × inside the field
        autoDetectCountry = true,      // SIM → network → locale → default
        enforceMaxLength = false,      // default true: cap digits at the country's max
        defaultCountryListStyle = CountryListStyle.Grid,   // or List
        showLabel = true,              // floating label (BottomSheet style)
        phoneFieldSize = PhoneFieldSize.Compact,
    ),
    label = "Mobile number",
    onDone = { submitForm() },         // only when isValid
)
```

## Colors, shapes and typography

```kotlin
PhoneNumberInput(
    colors = CCPDefaults.colors(
        containerColor = Color.White,
        borderColor = Color.Gray,
        errorBorderColor = Color.Red,
        errorColor = Color.Red,
        cursorColor = Color.Black,
        inputTextColor = Color.DarkGray,
        phoneHintColor = Color.LightGray,
    ),
    ccpConfig = CCPDefaults.defaultConfig(
        phoneInputShape = RoundedCornerShape(8.dp),
        borderWidth = 1.dp,
        showCountryFlag = true,
        showHeader = true,
        showCountriesHeaderDivider = true,
    ),
)
```

## Reference

### `PhoneNumberInput` parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `modifier` | `Modifier` | `Modifier` | Applied to the outer column |
| `state` | `PhoneState` | `rememberPhoneState()` | Hoistable state holder |
| `phoneHint` | `String` | `"Enter phone"` | Placeholder text |
| `label` | `String?` | `"Phone number"` | Floating label; rendered only when `CCPConfig.showLabel` is true |
| `onValueChange` | `(Phone) -> Unit` | `{}` | Called on every change |
| `onPhoneValueChange` | `(String, String, Boolean) -> Unit` | | **Deprecated.** Use `onValueChange` |
| `onDone` | `() -> Unit` | `{}` | Called on IME Done, only when valid |
| `value` | `String` | `""` | Initial number (E.164 or local) |
| `setCountry` | `String?` | `null` | ISO code or name to preselect |
| `countriesToShow` | `List<String>` | `emptyList()` | Allow list of ISO codes |
| `countriesExclude` | `List<String>` | `emptyList()` | ISO codes to hide |
| `pinnedCountries` | `List<String>` | `emptyList()` | ISO codes for the Suggested section |
| `isError` | `Boolean` | `false` | Error border and message |
| `errorMessage` | `String?` | `null` | Shown below the field when `isError` |
| `colors` | `CCPColors` | `CCPDefaults.colors()` | Color configuration |
| `ccpConfig` | `CCPConfig` | `CCPDefaults.defaultConfig()` | Behaviour and UI configuration |

### `PhoneState`

| Member | Type | Description |
|---|---|---|
| `activeCountry` | `Country?` | Selected country (legacy model). Never null in practice |
| `phoneNumber` | `String` | Raw digits |
| `phoneField` | `TextFieldValue` | The field's text and cursor; derived from `phoneNumber` |
| `formattedPhone` | `String` | International format, e.g. `+254 724 154 958` |
| `unformattedPhone` | `String` | E.164, e.g. `+254724154958`. Empty while unparseable |
| `isValid` | `Boolean` | Valid and dialable for the country |
| `validity` | `PhoneNumberValidity` | The granular reason behind `isValid`; see [Validation](../phone/validation.md#validity) |
| `value` | `PhoneNumberValue` | The full evaluated value, including the nullable E.164 form |
| `selectCountry(Country)` | | Set the country and reformat; the digits are kept and re-validated |
| `setCountryByCode(String)` | | Set the country by ISO code |
| `updatePhoneNumber(TextFieldValue)` | | Update the field and reformat |
| `parseAndSet(String)` | | Parse a full string; adopts the country when the input identifies one |
| `clearPhone()` | | Reset the number, keep the country |
| `toPhone()` | `Phone` | Snapshot of the current state |

### `Phone`

```kotlin
data class Phone(
    val formattedPhone: String,       // "+254 724 154 958"
    val phoneNumber: String,          // E.164 "+254724154958"
    val country: SelectedCountry?,    // name, code, dialCode, flag
    val isValid: Boolean,
)
```

### `CCPConfig`

| Property | Type | Default | Description |
|---|---|---|---|
| `autoDetectCountry` | `Boolean` | `false` | Detect the country from SIM / network / locale |
| `countryPickerStyle` | `CountryPickerStyle` | `BottomSheet` | `BottomSheet` or `Dropdown` |
| `defaultCountryListStyle` | `CountryListStyle` | `List` | `List` or `Grid` |
| `showClearButton` | `Boolean` | `false` | × button inside the input |
| `enforceMaxLength` | `Boolean` | `true` | Cap digits at the country's maximum |
| `readOnly` | `Boolean` | `false` | Disable the input and the selector |
| `showCountryFlag` | `Boolean` | `true` | Flag in the selector button |
| `showHeader` | `Boolean` | `true` | Letter headers in the country list |
| `showCountriesHeaderDivider` | `Boolean` | `true` | Divider next to letter headers |
| `showFlagCountryItem` | `Boolean` | `true` | Flag in each row |
| `showDialCodeCountryItem` | `Boolean` | `true` | Dial code in each row |
| `showLabel` | `Boolean` | `false` | Floating label notched into the outline (BottomSheet style) |
| `showPhonePrefixDivider` | `Boolean` | `true` | Divider between the prefix and the editor (BottomSheet style) |
| `phoneFieldSize` | `PhoneFieldSize` | `Regular` | `Regular`, `Compact` or `ExtraCompact` (BottomSheet style) |
| `borderWidth` | `Dp` | `0.dp` | Input border thickness |
| `phoneInputShape` | `Shape` | `RoundedCornerShape(16.dp)` | Input container shape |
| `phoneInputCornerRadius` | `Dp` | `16.dp` | Radius used by the default `phoneInputShape` |
| `countriesSheetShape` | `Shape` | top corners `16.dp` | Bottom sheet shape |
| `searchCornerRadius` | `Dp` | `16.dp` | Search field corner radius |
| `searchBorderWidth` | `Dp` | `1.dp` | Search field border thickness |
| `phoneHintStyle`, `searchHintStyle`, `headerStyle`, `countryItemNameTextStyle`, `countryItemDialCodeTextStyle` | `TextStyle` | Material defaults | Text styles |
| `countryItemShape` | `Shape` | `RoundedCornerShape(16.dp)` | Row shape |

### `CCPColors`

| Property | Default | Description |
|---|---|---|
| `containerColor` | `colorScheme.background` | Input background |
| `borderColor` | `colorScheme.outline` | Normal border |
| `errorBorderColor` | `colorScheme.error` | Error border |
| `errorColor` | `colorScheme.error` | Error message text |
| `cursorColor` | `colorScheme.primary` | Text cursor |
| `inputTextColor` | `colorScheme.onSurface` | Entered number |
| `phoneHintColor` | `colorScheme.onSurfaceVariant` | Placeholder |
| `countryCodeTextColor` | `colorScheme.onSurface` | Dial code in the selector |
| `countryChevronColor` | `colorScheme.onSurface` | Chevron icon |
| `ccpSheetColor` | `CCPDefaults.ccpSheetColor()` | Bottom sheet and dropdown colors (`CCPSheetColor`) |

### Standalone pickers

`CountriesDropdown(expanded, onDismiss, onSelectCountry, …)` and
`CountriesBottomSheet(sheetState, onSelectCountries, …)` are public for hosts that used them
directly. Both accept the same `countriesToShow` / `countriesExclude` / `pinnedCountries`,
`ccpColors` and `ccpConfig` parameters as `PhoneNumberInput`.
