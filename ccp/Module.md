# Module ccp

A Jetpack Compose library for country selection, phone country-code selection, phone number
validation and real-time formatting. Everything here is pure Compose state — no ViewModels, no
navigation coupling.

Start with [CountrySelector][com.ezzy.ccp.countrypicker.ui.CountrySelector] for any
single-country field, [MultiCountrySelector][com.ezzy.ccp.countrypicker.ui.MultiCountrySelector]
for several, and [PhoneNumberField][com.ezzy.ccp.countrypicker.ui.PhoneNumberField] for an
international phone number with E.164 output.

Guides, recipes and the migration guide live on the
[documentation site](https://ezekielwachira.github.io/Country-Code-Picker/).

# Package com.ezzy.ccp.countrypicker.ui

Composables: the selectors, the phone field, the bottom sheet and its building blocks (list, rows,
search field, region chips, empty/error/loading states, detection badge). All of them take their
colors, shapes, dimensions, typography and motion from `CountryPickerDefaults`.

# Package com.ezzy.ccp.countrypicker.state

State holders and configuration: `CountryPickerConfig` (how a picker behaves), `CountryPickerState`
(the sheet's transient state) with `rememberCountryPickerState`, and `PhoneNumberFieldState` with
`rememberPhoneNumberFieldState`.

# Package com.ezzy.ccp.countrypicker.model

The canonical `Country` model, `PhoneNumberValue`, `UiText`, and the small enums that describe
selection mode, search fields, supporting content and regions. Also the conversions between the
canonical model and the legacy `com.ezzy.ccp.model` types.

# Package com.ezzy.ccp.countrypicker.theme

Theming: `CountryPickerDefaults` and the value classes it produces — `CountryPickerColors`,
`CountryPickerShapes`, `CountryPickerDimensions`, `CountryPickerTypography`, `CountryPickerMotion` —
plus flag presentation (`CountryFlagShape`, `CountryFlagStyle`, `CountryFlagConfig`) and the phone
field's `PhoneNumberInputStyle`.

# Package com.ezzy.ccp.countrypicker.phone

Phone number formatting and validation on top of Google libphonenumber (`PhoneNumberFormatter`,
`PhoneNumberValidator`), and the optional, host-implemented verification flow
(`PhoneNumberVerificationHandler`, `PhoneVerificationController`, `PhoneVerificationState`).

# Package com.ezzy.ccp.countrypicker.data

Where countries come from: the bundled `DefaultCountryDataSource` (236 countries and territories),
the `CountryDataSource` interface for remote or localized sources, the caching `CountryRepository`,
and the ranked `CountrySearchEngine`.

# Package com.ezzy.ccp.countrypicker.detection

Country detection from device signals: the `CountryDetector` interface, `DefaultCountryDetector`
(SIM → network → locale, no location permission), and the result/behavior types that describe what
the picker does with a detected country.

# Package com.ezzy.ccp.countrypicker.persistence

Persisting recently selected countries: the `RecentCountryStore` interface and its file-backed,
in-memory and no-op implementations. Only ISO codes are ever stored.

# Package com.ezzy.ccp.components

The original `PhoneNumberInput` component and its dropdown/bottom-sheet pickers. Fully supported;
runs on the country-picker foundation internally.

# Package com.ezzy.ccp.model

Legacy model types used by `PhoneNumberInput`: `Phone`, `Country`, `SelectedCountry`, `CCPConfig`,
`CCPColors`.

# Package com.ezzy.ccp.state

Legacy state holders: `PhoneState` for `PhoneNumberInput`.

# Package com.ezzy.ccp.utils

Legacy defaults and helpers: `CCPDefaults`, `CCPSheetColor`.
