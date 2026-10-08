# Changelog

All notable changes to this project are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses
[Semantic Versioning](https://semver.org/spec/v2.0.0.html). While the version is below `1.0.0`,
breaking changes may land in a minor release; each one is listed under **Removed** or **Changed**
with the migration.

The public ABI is recorded in [`ccp/api/`](ccp/api) — `android/ccp.api` for the JVM and
`ccp.klib.api` for iOS — and checked on every pull request, so every entry under **Added**,
**Changed** or **Removed** below has a corresponding diff in those files.

## [Unreleased]

## [0.3.0] - 2026-10-08

Kotlin Multiplatform support for Android and iOS, and a complete visual redesign with a design system
behind it. **Breaking:** every component's styling parameters are replaced by a single `style`; see
**Removed** for the migration.

### Added

- **`CountryPickerStyle`, one value for everything visual** — colors, shapes, dimensions,
  typography, motion, elevation, layout and haptics — set once with `CountryPickerTheme(style)` or
  per component with `style =`.
- **Presets:** `CountryPickerStyles.signature()` (the new default: inset grouped lists, flag tiles,
  layered soft shadows), `material()`, `cupertino()` and `minimal()`. Each takes a brand accent,
  follows the host theme's light or dark mode, and comes in three densities (`CountryPickerDensity`).
- **Flags from [flagcdn.com](https://flagcdn.com)**, the new default (`CountryFlagSource.FlagCdn`):
  the same artwork on every platform, Kosovo included, in flagcdn's three shapes
  (`FlagImageShape.Waving`, `OriginalSameWidth`, `OriginalSameHeight`) and four formats
  (`FlagImageFormat.Png`, `WebP`, `Jpeg`, `Svg`), sized to the screen's density and cached in memory
  and on disk. The emoji stands in while a flag loads and when offline. `CountryFlagSource.Emoji`
  keeps everything offline. Loaded with Coil 3; no Coil type is part of the public API.
- **Flag frames** — `CountryFlagStyle.Tile`, `Circle`, `Rounded`, `Plain`, `Hidden` — independent of
  the artwork's shape.
- **Quick picks:** recent, suggested and detected countries as a carousel of tiles above the list
  (`QuickPicksStyle.Carousel`), as sections, or hidden. `CountryPickerState.quickPicks`.
- **"Did you mean"** suggestions when a search finds nothing, tolerant of typos and transpositions
  (`CountryPickerState.searchSuggestions`, `CountrySearchEngine.suggest`).
- **Adaptive presentation** (`PickerPresentation`): a bottom sheet on phones and a centered dialog on
  wide windows, or always one or the other, or a full-screen sheet. `CountryPickerPanel` is the
  picker with no container, for a route or a pane.
- **A sliding region filter** with per-region counts, an A–Z rail with a magnified letter bubble, an
  animated check that draws itself in, staggered list entrance, and a floating confirmation bar with
  stacked flags in multiple selection.
- **Phone field:** a floating label, ghost digits showing what is left to type, a progress hairline
  that turns green on a valid number, a `✓ Mobile` / `✓ Landline` badge (a compact check while
  editing, so the number keeps its room), a `variant` parameter, and a clear button shown while
  editing. Screen readers hear the field's validity as its state.
- **New selector variants:** `Elevated` (the default) and `Card`, with a soft focus ring, a shake on
  error and press feedback on every field.
- **Haptics** for selection, toggles, the A–Z rail, region changes, the selection limit and errors
  (`CountryPickerHaptics`).
- The sample app's **Showcase** tab: every option of the design system as a live control.
- **iOS support — the library is now Kotlin Multiplatform.** Every component, state holder and
  utility is in common code and runs on Android and iOS (`iosArm64`, `iosSimulatorArm64`) from
  Compose Multiplatform. Gradle consumers keep the same coordinate,
  `io.github.ezekielwachira:ccp`, in `commonMain` or in an Android-only module.
- **The same phone-number engine on both platforms.** iOS uses
  [libPhoneNumber-iOS](https://github.com/iziz/libPhoneNumber-iOS) 2.1.2, the Objective-C port of
  Google's libphonenumber, generated from the same metadata release as the Android dependency
  (libphonenumber 9.0.41, bumped from 9.0.14 to match). It is vendored, compiled into the iOS klib
  and namespaced with a `CCP` symbol prefix, so iOS apps need no CocoaPods, SwiftPM or linker setup
  and can still link their own copy of libPhoneNumber-iOS without symbol clashes. The common test
  suite runs on both platforms to keep the two engines in agreement.
- `rememberDefaultCountryDetector()` and `rememberDefaultRecentCountryStore()` create the
  platform's detector and recents store from common code. On iOS the detector reads the user's
  Region setting (iOS has no usable SIM or network country API); the store uses `NSUserDefaults`.
- `UiText.Custom`, an extension point for strings from sources other than literals and Compose
  resources.
- A shared `:sample` module and an Xcode project (`iosApp/`) running the same demo on iOS.
- **Localized country names, on by default.** Non-English names resolve through the platform's
  `Locale.getDisplayCountry`, so a French device shows "Allemagne" and a Japanese one "ドイツ" — in
  every language Android has data for, not only the ones the library ships strings for. English
  keeps the bundled dataset's names, which are curated to current ISO usage ("Cabo Verde",
  "Türkiye") where the platform's English is often behind and varies by OS version. The list is
  sorted with a locale-aware collator in every language, the English name is kept as a search alias,
  and the list rebuilds when the language changes at runtime.
- **Translations for seven locales** — Arabic, German, Spanish, French, Portuguese, Swahili and
  Simplified Chinese — complete, with the plural categories each language actually needs.
- **Right-to-left correctness.** Dial codes and phone numbers are pinned left-to-right, so `+254`
  no longer renders as `254+` inside RTL text. See `CountryPickerBidi`.
- **Autofill.** `PhoneNumberField` declares `ContentType.PhoneNumber` and
  `ContentType.PhoneNumberNational`, and an autofilled international number adopts the country it
  names rather than being read as national digits for whichever country was selected. Opt out with
  `autofillEnabled = false`.
- **Line-type restrictions.** `PhoneNumberType` and `allowedNumberTypes` let a field require a
  mobile number for SMS flows — `PhoneNumberType.SmsCapable` — instead of merely a valid one.
  `PhoneNumberValue.numberType` exposes the classification.
- **A–Z index rail.** `CountryPickerConfig.showAlphabetIndex` adds a scrubbable alphabet index to
  the sheet. `CountryIndexRail` and `rememberCountryListIndex` are public for custom lists.
- `DefaultCountryDataSource.findByIso2(code, locale)` and `defaultInitialCountry(locale)` for
  seeding a selector with a name that matches what the picker shows.
- `UiText.Plural` now accepts format arguments beyond the count.
- Maven Central publishing, a tag-driven release workflow, and a CI pipeline running build, unit
  tests, API check, lint, screenshot tests and instrumented tests on API 26 and 34.
- A screenshot suite (Roborazzi) covering every selector variant, the phone field in each size and
  error state, light and dark themes, RTL, and an Arabic locale.
- A sample-app tab demonstrating the modern API — localized names, mobile-only validation, the
  alphabet index and every selector variant.
- This changelog.

### Changed

- The library's Android manifest now declares `INTERNET`, for flag images. Apps using
  `CountryFlagSource.Emoji` everywhere can remove it with `tools:node="remove"`.
- `CountrySelectorVariant.Minimal` is now `Underlined`; the default variant is `Elevated`.
- Rows show dial codes by default (`CountryPickerLayout.showDialCode`), and the A–Z rail is on by
  default (`showAlphabetIndex`).
- `CountryFlag` takes a `source`, and its `style` is now the frame (`CountryFlagStyle`).
- **Strings are Compose Multiplatform resources.** `UiText.Resource` and `UiText.Plural` hold a
  `StringResource` / `PluralStringResource` (from the library's or the host's own `Res`), and
  `CountryRegion.labelRes`, `CountrySectionKind.titleRes` and `CountryDetectionSource.labelRes` are
  `StringResource`s. On Android, `UiText.resource(R.string.x)` and `UiText.plural(R.plurals.x, n)`
  still work through Android-only overloads — add `import com.ezzy.ccp.countrypicker.model.resource`
  (or `.plural`) where they are called. Library defaults can no longer be replaced by redefining a
  `ccp_*` string in the app's `res/values`; pass a `UiText` to the component instead.
- **`DefaultCountryDataSource.findByIso2(code, locale)`, `defaultInitialCountry(locale)` and
  `localizedNames(locale)` take Compose's multiplatform `androidx.compose.ui.text.intl.Locale`.**
  Android overloads accepting `java.util.Locale` remain; import them from
  `com.ezzy.ccp.countrypicker.data` where they are called.
- **`DefaultCountryDetector(context)` and `DefaultRecentCountryStore(context)` are now factory
  functions** rather than constructors. Kotlin call sites are unchanged; Java callers use
  `DefaultCountryDetector_androidKt` / `DefaultRecentCountryStore_androidKt`.
- `PhoneNumberFormatter.parse`, which returns libphonenumber's own `PhoneNumber`, is now an
  Android-only extension function (import `com.ezzy.ccp.countrypicker.phone.parse`).
- The library no longer depends on `androidx.appcompat`, `androidx.activity:activity-compose` or
  `androidx.core:core-ktx`.
- Toolchain: Kotlin 2.4.20, Android Gradle Plugin 9.4, Gradle 9.6, Compose Multiplatform 1.12.1
  (Jetpack Compose 1.12.1 on Android), compileSdk 37. The binary-compatibility-validator plugin is
  replaced by Kotlin's built-in ABI validation: `./gradlew :ccp:checkKotlinAbi` /
  `:ccp:updateKotlinAbi`.
- **Emoji flags now fall back cleanly on devices that cannot draw them.** A flag emoji is a pair of
  regional-indicator code points, and many OEM, Android TV and Wear fonts have had those glyphs
  stripped — the string is present and only the rendering fails, so a `flag != null` check could not
  detect it and the picker showed a column of boxes. The font is now probed once and an ISO-code
  badge is drawn instead.
- Pasting or typing an international number into `PhoneNumberField` or the legacy `PhoneNumberInput`
  switches the field to the country it names. Previously the `+` was stripped and the calling code
  was read as part of the subscriber number, producing a plausible but wrong value.
- The library is now compiled in Kotlin explicit-API mode and targets JVM 11.
- Lint runs with `warningsAsErrors` and no baseline; 14 pre-existing errors were fixed rather than
  recorded.
- The legacy `PhoneNumberInput` accessibility label is now a localized resource
  (`ccp_phone_input_a11y`). The English wording is unchanged.

### Removed

- `CountryPickerDefaults.colors()`, `shapes()`, `dimensions()`, `typography()` and `motion()`, and the
  `colors` / `shapes` / `dimensions` / `typography` / `motion` parameters on every component: pass a
  `CountryPickerStyle` instead — `CountryPickerStyles.signature()` and `copy()` for overrides.
- `CountryPickerTokens`: success colors are roles of `CountryPickerColors`.
- `CountryFlagConfig` and `CountryFlagShape`: use `CountryPickerLayout.flagStyle` and `flagSource`.
- `CountrySelectorContentConfig`, `CountrySelectorDefaults` and the `contentConfig` parameter: use
  `labelMode` and `supportingContent` on `CountrySelector`.
- Presentation flags on `CountryPickerConfig` — `showIsoCode`, `showDialCode`, `showFlag`,
  `flagShape`, `rowFlagStyle`, `showRegionFilters`, `showAlphabetIndex`, `highlightSearchMatches`,
  `showResultCount`, `showCurrentSelection` — moved to `CountryPickerLayout`, which now owns
  everything about how the picker looks. The "Current selection" card is replaced by the selected
  group at the top of the list.
- `UiText.resolve(context: Context)`. Resolve inside composition with `UiText.resolve()`; outside
  it, Compose resources' `getString(…)` reads the library's or your own `Res`.
- **The bundled flag `ImageVector`s** (131 of them) and `CountryFlagVectors`, `VectorCountryFlag`
  and `CountryFlagFallback`. They covered barely half the dataset and nothing rendered them by
  default — every component draws the platform emoji glyph. To supply your own artwork, pass
  `flagContent` to any selector, sheet or list.
- `com.ezzy.ccp.model.Country.flag` (the legacy `ImageVector` field) is now always `null` and
  deprecated. The parameter is kept so existing `Country(...)` and `copy(...)` calls still compile;
  use `code.countryToFlagEmoji()` or the `CountryFlag` composable instead.
  The non-flag `EzzyIcons` UI icons (`Close`, `ChevronDown`, `Search`, `Grid`, `List`) are unaffected.

### Fixed

- Country names were only ever English. `DefaultCountryDataSource.localizedNames` existed and was
  complete, but nothing called it, so the picker showed English names inside otherwise translated
  UI.
- `rememberCountryPickerState` previews applied their region filter and search query on every
  recomposition rather than once.
- `PhoneNumberValidator.errorMessage` now uses a plural for the expected-digit count, so a
  single-digit region reads "1 digit after +xx" rather than "1 digits after +xx".
- `scripts/prepareJitpackEnvironment.sh` — `jitpack.yml` referenced a script that had never been
  committed, so JitPack builds failed during configuration.

## [0.2.0]

- Full country picker: searchable sheet over 236 countries and territories, single and
  multi-selection, region filters, Selected/Recent/Suggested grouping, and ranked accent-insensitive
  search.
- Modernized phone input: `PhoneNumberField` with libphonenumber-backed validation, live formatting,
  E.164 output and optional verification.
- Full, compact, flag-only and phone-prefix selector variants.
- Country detection from SIM → network → locale, with no location permission.
- Documentation site (MkDocs + Dokka) published to GitHub Pages.

## [0.1.x]

Early releases of the legacy `PhoneNumberInput` API: bottom-sheet and dropdown country pickers, grid
layout, pinned countries, max-length enforcement and the country-detection utility. See
[the migration guide](docs/legacy/migration.md) for moving off this API.

[Unreleased]: https://github.com/EzekielWachira/Country-Code-Picker/compare/v0.3.0...HEAD
[0.3.0]: https://github.com/EzekielWachira/Country-Code-Picker/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/EzekielWachira/Country-Code-Picker/releases/tag/v0.2.0
