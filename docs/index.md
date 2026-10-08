---
hide:
  - navigation
---

# Country Code Picker

[![JitPack](https://jitpack.io/v/EzekielWachira/Country-Code-Picker.svg)](https://jitpack.io/#EzekielWachira/Country-Code-Picker)
[![License: MIT](https://img.shields.io/badge/License-MIT-teal.svg)](https://github.com/EzekielWachira/Country-Code-Picker/blob/main/LICENSE.md)
[![API 24+](https://img.shields.io/badge/API-24%2B-teal.svg)](https://developer.android.com/about/versions/nougat)
[![Platforms: Android | iOS](https://img.shields.io/badge/platforms-Android%20%7C%20iOS-teal.svg)](getting-started.md)

A lightweight, fully customizable **Compose Multiplatform** library for **Android and iOS**: country
selection, country-code selection, phone number validation, and real-time formatting based on
country-specific rules — written once in common code.

Everything is plain Compose state: no ViewModels, no navigation coupling, and every component is a
*controlled* component whose value you own.

<div class="grid cards" markdown>

-   :material-earth:{ .lg .middle } **Country picker**

    ---

    Searchable list of 236 countries and territories with flags and dial codes. Full, compact,
    flag-only and dial-code selector variants, region filters, recents and suggestions.

    [:octicons-arrow-right-24: Country selector](country-picker/selector.md)

-   :material-checkbox-multiple-marked:{ .lg .middle } **Single and multi-selection**

    ---

    One country, or several with Cancel / Reset / Confirm. The confirmed selection changes only when
    the user taps Confirm.

    [:octicons-arrow-right-24: Multi-selection](country-picker/multi-select.md)

-   :material-phone:{ .lg .middle } **Phone number field**

    ---

    Country prefix and number editor in one outlined field. Progressive as-you-type formatting and
    E.164 output from Google libphonenumber, never from string concatenation.

    [:octicons-arrow-right-24: Phone number field](phone/field.md)

-   :material-shield-check:{ .lg .middle } **Validation and verification**

    ---

    Granular validity that tells *still typing* apart from *wrong*, plus an optional, decoupled OTP
    verification flow backed by your own SMS or Firebase provider.

    [:octicons-arrow-right-24: Validation](phone/validation.md) ·
    [:octicons-arrow-right-24: Verification](phone/verification.md)

-   :material-map-marker-radius:{ .lg .middle } **Country detection**

    ---

    SIM → network → locale, with no location permission. Detection is a suggestion and never
    overrides a country the user chose.

    [:octicons-arrow-right-24: Country detection](country-picker/detection.md)

-   :material-palette:{ .lg .middle } **Themeable and accessible**

    ---

    Colors, shapes, dimensions, typography and motion all flow from `CountryPickerDefaults` and
    follow your Material 3 theme. 48dp touch targets, screen-reader friendly, no color-only signalling.

    [:octicons-arrow-right-24: Theming](theming.md) ·
    [:octicons-arrow-right-24: Accessibility](accessibility.md)

</div>

## Two layers, one foundation

- **Country Picker** (`com.ezzy.ccp.countrypicker`) is the general-purpose foundation: selectors,
  the bottom sheet, search, configuration, theming, detection, phone formatting and verification.
  This is the API to build on.
- **`PhoneNumberInput`** (`com.ezzy.ccp.components`) is the original phone-number component. It runs
  on the Country Picker foundation internally, so existing call sites keep working unchanged. See the
  [legacy API](legacy/index.md) and the [migration guide](legacy/migration.md).

## Install

Add the dependency from Maven Central — to `commonMain` in a Kotlin Multiplatform module, or to
`dependencies` in an Android-only one. Nothing else is needed on iOS. Full details in
[Getting started](getting-started.md).

```kotlin title="build.gradle.kts"
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.ezekielwachira:ccp:<LATEST_VERSION>")
        }
    }
}
```

## A first look

```kotlin
var country by rememberSaveable { mutableStateOf<Country?>(null) }

CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    label = UiText.of("Country of residence"),
)

val phoneState = rememberPhoneNumberFieldState(
    initialCountry = DefaultCountryDataSource.findByIso2("KE")!!,
)

PhoneNumberField(
    state = phoneState,
    onValueChange = { value ->
        value.e164Number   // "+254712345678", or null while unparseable
        value.isValid      // true only for a real, dialable number
    },
)
```

## Demo

A short recording of the sample app is on the
[repository README](https://github.com/EzekielWachira/Country-Code-Picker#demo). The sample screen
that reproduces the flow lives in
[`YourDetailsSampleScreen.kt`](https://github.com/EzekielWachira/Country-Code-Picker/blob/main/ccp/src/commonMain/kotlin/com/ezzy/ccp/countrypicker/sample/YourDetailsSampleScreen.kt),
and every state described in these docs has a matching `@Preview` in
[`CountryPickerPreviews.kt`](https://github.com/EzekielWachira/Country-Code-Picker/blob/main/ccp/src/commonMain/kotlin/com/ezzy/ccp/countrypicker/sample/CountryPickerPreviews.kt).

## License

Released under the [MIT License](https://github.com/EzekielWachira/Country-Code-Picker/blob/main/LICENSE.md).
Copyright © 2025 Ezekiel Wachira.
