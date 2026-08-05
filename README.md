# Country Code Picker

[![](https://jitpack.io/v/EzekielWachira/Country-Code-Picker.svg)](https://jitpack.io/#EzekielWachira/Country-Code-Picker)

A lightweight, fully customizable Jetpack Compose library for country selection, country code selection, phone number validation, and real-time formatting based on country-specific rules.

The library ships two layers:

- **[Country Picker](#country-picker-library)** — a general-purpose country-selection foundation:
  full/compact/flag-only/phone-prefix selectors, single and multi-selection, search, region filters,
  recents and suggestions, country detection, and an optional decoupled phone-verification flow.
- **[`PhoneNumberInput`](#legacy-phonenumberinput-api)** — the original phone-number component. It now
  runs on the Country Picker foundation internally, so existing call sites keep working unchanged; see
  [Migrating from `PhoneNumberInput`](#migrating-from-phonenumberinput) if you want to move to the newer
  API.

## Features

- **Country Picker** – Searchable list of 236 countries and territories with flags and dial codes
- **Single and multi-selection** – One country, or several with Cancel / Reset / Confirm
- **Full, compact, flag-only and phone-prefix selectors** – Same sheet, different footprints
- **Region filters** – Africa, Americas, Asia, Europe, Oceania, plus an "All" view
- **Selected / Recent / Suggested grouping** – No duplicates across sections
- **Ranked search** – By name, ISO alpha-2/3, dial code, or alias — accent-insensitive
- **Phone Number Validation** – Country-aware validation powered by Google libphonenumber
- **Real-time Formatting** – Formats to E.164, international, and national formats as the user types
- **Phone verification** – Optional, via a host-supplied `PhoneNumberVerificationHandler`
- **Max Length Enforcement** – Caps input at the correct digit count for the selected country
- **Auto Country Detection** – Detects from SIM → network → locale, with fallback; never overrides an
  explicit user choice
- **State Hoisting** – Expose `CountryPickerState` / `PhoneState` to the caller for external control
- **Error / disabled / loading / success states** – On every selector variant
- **Themeable** – Colors, shapes, dimensions and typography all flow from `CountryPickerDefaults`
- **Accessibility** – Full TalkBack / content description support, 48dp touch targets, no color-only
  signaling
- **Compose-First API** – No ViewModels, no navigation coupling, pure Compose state

## Installation

### Step 1 — Add JitPack

In your root `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2 — Add the dependency

```gradle
dependencies {
    implementation 'com.github.EzekielWachira:Country-Code-Picker:<LATEST_VERSION>'
}
```

## Country Picker library

Everything below lives under `com.ezzy.ccp.countrypicker` — the country model, search, state, theme
and UI are all reusable independently of phone numbers. A sample screen reproducing the flow below is
in [`YourDetailsSampleScreen`](ccp/src/main/java/com/ezzy/ccp/countrypicker/sample/YourDetailsSampleScreen.kt),
and every state described here has a matching `@Preview` in
[`CountryPickerPreviews.kt`](ccp/src/main/java/com/ezzy/ccp/countrypicker/sample/CountryPickerPreviews.kt).

### A basic country selector

```kotlin
var country by rememberSaveable { mutableStateOf<Country?>(null) }

CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    label = UiText.of("Country of residence"),
)
```

`CountrySelector` is fully controlled: `selectedCountry` is the source of truth, and the component never
mutates it — a selection only ever arrives through `onCountrySelected`, carrying the complete `Country`
(ISO alpha-2/3, dial code, region, flag), never just a name or a code.

### Compact and flag-only variants

Same sheet, same behavior, smaller footprint — pick with `variant`:

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    variant = CountrySelectorVariant.Compact,   // 🇩🇪 Germany ˅
    label = null,
)

CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    variant = CountrySelectorVariant.FlagOnly,  // 🇩🇪 ˅
    label = null,
)
```

The flag-only variant has no visible text, so it carries its own content description automatically —
`"Selected country: Germany. Double tap to change."` — no extra wiring needed.

### The phone country-code picker

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    variant = CountrySelectorVariant.DialCode,  // 🇰🇪 +254 ˅
    label = null,
    config = CountryPickerDefaults.phoneConfig(),
)
```

In practice you won't call this directly — [`PhoneNumberField`](#the-phone-number-field) already embeds
it next to the number input.

### The phone number field and E.164 output

```kotlin
val phoneState = rememberPhoneNumberFieldState(initialCountry = kenya)

PhoneNumberField(
    state = phoneState,
    onValueChange = { value: PhoneNumberValue ->
        value.e164Number           // "+254712345678", or null while unparseable
        value.internationalNumber  // "+254 712 345678"
        value.isValid              // true only for a real, dialable number
    },
)
```

`PhoneNumberValue.e164Number` comes from libphonenumber, not from concatenating a dial code onto typed
digits — that concatenation is wrong for any region with a national trunk prefix (a UK mobile typed as
`07400 123456` is `+447400123456`, not `+4407400123456`). Never rebuild it by hand.

Changing the selected country keeps the digits the user already typed, reformats them for the new
region, and **revalidates** — a number valid in one country is never carried over as still-valid in
another.

### Validating a phone number

```kotlin
when (phoneState.value.validity) {
    PhoneNumberValidity.Empty -> Unit                 // nothing typed; show nothing
    PhoneNumberValidity.Incomplete -> Unit             // still typing; show nothing
    PhoneNumberValidity.Valid -> submit()
    else -> showError(PhoneNumberValidator.errorMessage(phoneState.value))
}
```

`PhoneNumberField` already shows/hides its own error text this way; reach for `PhoneNumberValidator`
directly only when you need the result before the user submits.

### Integrating phone verification

The library ships no verification backend on purpose — implement the one contract against your own
SMS/Firebase/whatever provider:

```kotlin
class MyVerificationHandler(private val api: MyAuthApi) : PhoneNumberVerificationHandler {
    override suspend fun requestVerification(phoneNumber: PhoneNumberValue) =
        api.sendCode(phoneNumber.e164Number!!).toRequestResult()

    override suspend fun verifyCode(verificationId: String, code: String) =
        api.checkCode(verificationId, code).toVerificationResult()

    override suspend fun resendCode(verificationId: String) =
        api.resendCode(verificationId).toRequestResult()
}

val verification = rememberPhoneVerificationController(MyVerificationHandler(api))

PhoneNumberField(
    state = phoneState,
    onValueChange = {},
    verificationController = verification,
)

Button(
    enabled = phoneState.value.isValid,
    onClick = { scope.launch { verification.requestCode(phoneState.value) } },
) { Text("Send code") }

when (val state = verification.state) {
    is PhoneVerificationState.CodeSent -> OtpEntry(onSubmit = { scope.launch { verification.submitCode(it) } })
    is PhoneVerificationState.Verified -> Text("Verified: ${state.e164Number}")
    is PhoneVerificationState.Failed -> Text(state.message.resolve())
    else -> Unit
}
```

Editing the phone number after a successful verification resets the flow to `Idle` automatically — a
form can never submit "verified" for a number the user has since changed.

### Allowed / excluded countries and region filters

```kotlin
CountryPickerDefaults.config(
    allowedCountryCodes = setOf("US", "CA", "GB", "KE"),   // null (default) = every country
    excludedCountryCodes = setOf("KP", "IR"),               // exclusion always wins
    showRegionFilters = true,
)
```

An **empty** `allowedCountryCodes` set is a real, deliberate configuration — it allows nothing, and the
sheet shows its own "no countries available" state rather than silently falling back to everything.

### Suggested and recent countries

```kotlin
CountryPickerDefaults.config(
    suggestedCountryCodes = listOf("US", "GB", "KE"),  // your call — the library assumes nothing
)

CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    recentCountryStore = DefaultRecentCountryStore(context),  // omit to persist nothing
)
```

Selected, Recent and Suggested are deduplicated against each other and against "All countries" — a
selected Germany never also shows up under Suggested.

### Multi-selection

```kotlin
var markets by rememberSaveable { mutableStateOf(emptySet<Country>()) }

MultiCountrySelector(
    selectedCountries = markets,
    onSelectionConfirmed = { markets = it },   // fires only when the user taps Confirm
    config = CountryPickerDefaults.multiSelectConfig(
        minimumSelectionCount = 1,
        maximumSelectionCount = 5,
    ),
)
```

Ticking rows edits a *pending* selection inside the sheet; Cancel and dismiss discard it, so
`selectedCountries` never changes except through `onSelectionConfirmed`.

### Country detection

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    detector = DefaultCountryDetector(context),   // SIM → network → locale; no location permission
    detectionBehavior = CountryDetectionBehavior.ShowBadge,
)
```

Detection never overrides a selection the user already made. `ShowBadge` applies the guess and labels
it ("From your SIM card · tap to change"); `AskFirst` surfaces it as a suggestion the user must accept;
`Silent` applies it with no explanation.

### Customizing colors, shapes, typography and rows

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    colors = CountryPickerDefaults.colors(selectorContainer = MyBrand.fieldBackground),
    shapes = CountryPickerDefaults.shapes(selectorOutlined = RoundedCornerShape(4.dp)),
    listItemContent = { scope -> MyCustomRow(scope.country, scope.selected, scope.onClick) },
)
```

Every default reads from `MaterialTheme.colorScheme`/`.typography`, so the picker follows your app's
light/dark theme with no configuration at all; override only what actually needs to differ.

## Legacy `PhoneNumberInput` API

The original phone-number component. Still fully supported — it now runs on the Country Picker
foundation internally (ranked search, the full 236-country dataset, `AsYouTypeFormatter`-based
grouping), but its public API is unchanged.

### Basic usage

```kotlin
PhoneNumberInput(
    onValueChange = { phone ->
        // phone.formattedPhone  → "+254 724 154 958"
        // phone.phoneNumber     → "+254724154958" (E.164)
        // phone.country         → SelectedCountry(name, code, dialCode, flag)
        // phone.isValid         → true / false
    }
)
```

### State hoisting

Hoist `PhoneState` when you need to read state or drive the field from outside the composable — for example to clear it after form submission, or to validate on button tap.

```kotlin
val phoneState = rememberPhoneState()

PhoneNumberInput(
    state = phoneState,
    onValueChange = { phone -> ... }
)

Button(onClick = {
    if (phoneState.isValid) submit(phoneState.toPhone())
    else phoneState.clearPhone()
}) {
    Text("Submit")
}
```

### Picker style — bottom sheet vs dropdown

```kotlin
// Default: full-screen bottom sheet
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(
        countryPickerStyle = CountryPickerStyle.BottomSheet
    )
)

// Compact inline dropdown (better for tablets / landscape)
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(
        countryPickerStyle = CountryPickerStyle.Dropdown
    )
)
```

### Error state

```kotlin
var isError by remember { mutableStateOf(false) }

PhoneNumberInput(
    isError = isError,
    errorMessage = "Please enter a valid phone number",
    onValueChange = { phone ->
        isError = !phone.isValid && phone.phoneNumber.isNotEmpty()
    }
)
```

### Pre-fill and country override

```kotlin
PhoneNumberInput(
    value = "+254724154958",   // pre-fill in E.164 format; country is auto-detected
    setCountry = "KE",        // or force a specific country (overrides auto-detection)
)
```

### Country filtering

```kotlin
PhoneNumberInput(
    // Show only these countries
    countriesToShow = listOf("US", "GB", "KE", "FR", "AU"),

    // Or hide specific ones
    countriesExclude = listOf("KP", "IR"),

    // Pin frequently-used countries to the top of the picker
    pinnedCountries = listOf("US", "GB", "KE"),
)
```

### Clear button

```kotlin
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(showClearButton = true)
)
```

### Auto country detection

```kotlin
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(autoDetectCountry = true)
    // Priority: SIM → network → configuration locale → default locale → US
)
```

### Max length enforcement

Input is capped at the correct digit count for the selected country by default. Disable it if needed:

```kotlin
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(enforceMaxLength = false)
)
```

### Default country list layout

```kotlin
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(
        defaultCountryListStyle = CountryListStyle.Grid // or CountryListStyle.List
    )
)
```

### Keyboard Done action

```kotlin
PhoneNumberInput(
    onDone = {
        // Called only when isValid == true and the user taps Done
        submitForm()
    }
)
```

### Customizing colors

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
    )
)
```

### Customizing shapes and typography

```kotlin
PhoneNumberInput(
    ccpConfig = CCPDefaults.defaultConfig(
        phoneInputShape = RoundedCornerShape(8.dp),
        borderWidth = 1.dp,
        showCountryFlag = true,
        showHeader = true,
        showCountriesHeaderDivider = true,
        readOnly = false,
    )
)
```

### API Reference

### `PhoneNumberInput` parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `modifier` | `Modifier` | `Modifier` | Applied to the outer Column |
| `state` | `PhoneState` | `rememberPhoneState()` | Hoistable state holder |
| `phoneHint` | `String` | `"Enter phone"` | Placeholder text |
| `onValueChange` | `(Phone) -> Unit` | `{}` | Called on every change |
| `onDone` | `() -> Unit` | `{}` | Called on IME Done (only when valid) |
| `value` | `String` | `""` | Initial phone number (E.164 or local) |
| `setCountry` | `String?` | `null` | ISO code or name to preselect |
| `countriesToShow` | `List<String>` | `emptyList()` | Whitelist of ISO codes |
| `countriesExclude` | `List<String>` | `emptyList()` | Blacklist of ISO codes |
| `pinnedCountries` | `List<String>` | `emptyList()` | ISO codes pinned to top of picker |
| `isError` | `Boolean` | `false` | Show error border and message |
| `errorMessage` | `String?` | `null` | Text shown below field when `isError` |
| `colors` | `CCPColors` | `CCPDefaults.colors()` | Color configuration |
| `ccpConfig` | `CCPConfig` | `CCPDefaults.defaultConfig()` | Behavior and UI configuration |

### `PhoneState`

| Member | Type | Description |
|---|---|---|
| `activeCountry` | `Country?` | Currently selected country |
| `phoneNumber` | `String` | Raw digit string |
| `formattedPhone` | `String` | International format e.g. `+254 724 154 958` |
| `unformattedPhone` | `String` | E.164 format e.g. `+254724154958` |
| `isValid` | `Boolean` | Whether the number is valid for the country |
| `selectCountry(Country)` | `fun` | Set active country and reformat |
| `setCountryByCode(String)` | `fun` | Set country by ISO code |
| `updatePhoneNumber(TextFieldValue)` | `fun` | Update field and reformat |
| `parseAndSet(String)` | `fun` | Parse E.164 string, auto-detect country |
| `clearPhone()` | `fun` | Reset field and all derived state |
| `toPhone()` | `fun` | Snapshot current state as `Phone` |

### `Phone`

```kotlin
data class Phone(
    val formattedPhone: String,       // e.g. "+254 724 154 958"
    val phoneNumber: String,          // E.164 e.g. "+254724154958"
    val country: SelectedCountry?,    // name, code, dialCode, flag
    val isValid: Boolean
)
```

### `CCPConfig` options

| Property | Type | Default | Description |
|---|---|---|---|
| `autoDetectCountry` | `Boolean` | `false` | Detect country from SIM / locale |
| `countryPickerStyle` | `CountryPickerStyle` | `BottomSheet` | `BottomSheet` or `Dropdown` |
| `defaultCountryListStyle` | `CountryListStyle` | `List` | `List` or `Grid` |
| `showClearButton` | `Boolean` | `false` | Show × button inside input |
| `enforceMaxLength` | `Boolean` | `true` | Cap digits at country's max |
| `readOnly` | `Boolean` | `false` | Disable input and country selector |
| `showCountryFlag` | `Boolean` | `true` | Flag emoji in the selector button |
| `showHeader` | `Boolean` | `true` | Letter headers in country list |
| `showCountriesHeaderDivider` | `Boolean` | `true` | Divider next to letter headers |
| `showFlagCountryItem` | `Boolean` | `true` | Flag in each country row |
| `showDialCodeCountryItem` | `Boolean` | `true` | Dial code in each country row |
| `borderWidth` | `Dp` | `0.dp` | Input field border thickness |
| `phoneInputShape` | `Shape` | `RoundedCornerShape(16.dp)` | Input container shape |
| `countriesSheetShape` | `Shape` | `RoundedCornerShape(top=16.dp)` | Bottom sheet shape |
| `searchCornerRadius` | `Dp` | `16.dp` | Search field corner radius |
| `searchBorderWidth` | `Dp` | `1.dp` | Search field border thickness |

### `CCPColors` options

| Property | Default | Description |
|---|---|---|
| `containerColor` | `colorScheme.background` | Input background |
| `borderColor` | `colorScheme.outline` | Normal border |
| `errorBorderColor` | `colorScheme.error` | Error state border |
| `errorColor` | `colorScheme.error` | Error message text |
| `cursorColor` | `colorScheme.primary` | Text cursor |
| `inputTextColor` | `colorScheme.onSurface` | Entered number text |
| `phoneHintColor` | `colorScheme.onSurfaceVariant` | Placeholder text |
| `countryCodeTextColor` | `colorScheme.onSurface` | Dial code in selector |
| `countryChevronColor` | `colorScheme.onSurface` | Chevron icon |
| `ccpSheetColor` | — | All bottom sheet / dropdown colors (see `CCPSheetColor`) |

## Migrating from `PhoneNumberInput`

You do not have to migrate — `PhoneNumberInput` is fully supported and now runs on the Country Picker
foundation internally, so it already benefits from the bigger dataset, ranked search and progressive
formatting without any change on your part.

Move to the newer API when you want things `PhoneNumberInput` doesn't expose: multi-selection, region
filters, recents/suggestions, or phone verification. The building blocks map like this:

| Old | New |
|---|---|
| `PhoneNumberInput(...)` | `PhoneNumberField(state = rememberPhoneNumberFieldState(...), ...)` |
| `PhoneState` | `PhoneNumberFieldState` (`nationalDigits`, `value: PhoneNumberValue`, `selectCountry`, `clear`) |
| `Phone` | `PhoneNumberValue` (`e164Number`, `internationalNumber`, `isValid`, `validity`) |
| `Country` (`com.ezzy.ccp.model`) | `Country` (`com.ezzy.ccp.countrypicker.model`) — adds ISO alpha-3, region, aliases |
| `countriesToShow` / `countriesExclude` | `CountryPickerConfig.allowedCountryCodes` / `excludedCountryCodes` |
| `pinnedCountries` | `CountryPickerConfig.suggestedCountryCodes` |
| `CCPConfig.autoDetectCountry` | `detector = DefaultCountryDetector(context)` |
| `CCPColors` / `CCPConfig` shape params | `CountryPickerColors` / `CountryPickerShapes` via `CountryPickerDefaults` |
| — (did not exist) | `MultiCountrySelector`, region filters, `PhoneNumberVerificationHandler` |

A couple of behaviors changed as part of the underlying migration — both are described in the doc
comments on [`countryList`](ccp/src/main/java/com/ezzy/ccp/data/Data.kt) and
[`PhoneState`](ccp/src/main/java/com/ezzy/ccp/state/PhoneState.kt):

- The bundled country list grew from ~200 to 236 entries, and a few names now follow current ISO usage
  (`"Czech Republic"` → `"Czechia"`, `"Turkey"` → `"Türkiye"`) — former names remain searchable as
  aliases.
- Dial codes for NANP territories no longer contain hyphens (`"+1-809"` → `"+1809"`); the hyphenated
  form was not valid and broke E.164 normalization.
- The field now groups digits as you type (previously it stayed ungrouped until the number parsed
  cleanly), and `unformattedPhone`/E.164 output is empty rather than echoing raw input while a number
  is unparseable.

## Demo

https://github.com/user-attachments/assets/f2c7c5b9-85dc-4e4a-9909-4ae57490d561

## License

```
Copyright (c) 2025 Ezekiel Wachira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
