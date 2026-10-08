# Localization and right-to-left

Every user-facing string in the library is a Compose Multiplatform resource, resolved the same way
on Android and iOS. Nothing is hardcoded in a composable, so a host can override any of it, and the
library ships translations for seven locales.

## Bundled translations

| Locale | Language |
| --- | --- |
| `values` | English (source) |
| `values-ar` | Arabic |
| `values-de` | German |
| `values-es` | Spanish |
| `values-fr` | French |
| `values-pt` | Portuguese |
| `values-sw` | Swahili |
| `values-zh-rCN` | Simplified Chinese |

All seven are complete — every string, plural and string-array in the source locale has a
translation, with the plural categories each language actually needs (six for Arabic, one for
Chinese, two for the rest). A device in any other language falls back to English.

!!! note "Review welcome"
    The non-English translations have not been reviewed by native speakers. If something reads
    wrong in your language, a pull request correcting it is very welcome — the strings live in
    `ccp/src/commonMain/composeResources/values-*/strings.xml`.

## Country names

Country names are **not** in those files, and they are not limited to the seven locales above. They
are resolved through the platform's own region names — `Locale.getDisplayCountry` on Android,
`NSLocale.localizedStringForCountryCode` on iOS — so a French device shows "Allemagne" for `DE` and a
Japanese device shows "ドイツ" — in every language the platform has data for, without the library
shipping a 236-row translation table per language.

This is on by default. The list is also sorted with a locale-aware collator (`java.text.Collator` on
Android, Foundation's locale-aware comparison on iOS), so Germany files
under **A** in French and **G** in English, and "Åland Islands" files under **A** rather than past
**Z** where a plain string sort puts it.

English is the exception: it keeps the bundled dataset's names rather than going through the
platform. The dataset is curated to current ISO usage — "Cabo Verde", "Türkiye", "Czechia" — and
the platform's English is frequently behind it ("Cape Verde", "Turkey") by an amount that varies
with the device's ICU version. Routing English through the platform would undo that curation, and do
it differently on different OS releases. For every other language ICU is the only source there
is, and a slightly dated translation beats an English name.

Search still matches the English name: it is kept as a hidden alias on every localized country, so
"Allemagne", "Germany" and "deutschland" all reach `DE`.

### What stays English, and why

`DefaultCountryDataSource.countries`, `byIso2` and `findByIso2(code)` keep English names. They are
the identity and lookup surface — the legacy `countryList` is derived from them, and things are
keyed off them — and a name that moves when the user switches language is a key that stops working.

That matters in exactly one place: seeding a selector with a country the user will *see*.

```kotlin
// Shows "Germany" even on a French device, until the user opens the sheet.
var country by remember { mutableStateOf(DefaultCountryDataSource.findByIso2("DE")) }

// Shows "Allemagne", matching the sheet. (Locale is androidx.compose.ui.text.intl.Locale; on
// Android a java.util.Locale overload is also available.)
var country by remember { mutableStateOf(DefaultCountryDataSource.findByIso2("DE", Locale.current)) }
```

Identity is unaffected either way — `Country` compares on `iso2Code` alone, so a localized instance
and an English one are equal and interchangeable as map keys or set members.

### Changing language at runtime

The list rebuilds. The repository cache is keyed by locale, and the picker's load effect is keyed on
the composition's locale — on Android the configuration locale, so per-app languages
(`AppCompatDelegate.setApplicationLocales`) and `android:configChanges="locale"` are both handled;
neither recreates the Activity, and relying on that recreation would leave the sheet in the old
language. iOS restarts the app when the language changes.

## Overriding a string

Two levels, depending on scope.

Pass a `UiText` at the call site:

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    label = UiText.of("Nationality"),                      // a literal
    placeholder = UiText.resource(Res.string.pick_market), // your own Compose resource
)
```

In an Android-only app whose strings live in `res/values`, `UiText.resource(R.string.pick_market)`
works too (import `com.ezzy.ccp.countrypicker.model.resource`).

Compose Multiplatform resources are scoped to the module that declares them, so redeclaring a
`ccp_*` string in your app's resources does **not** change the library's default. To change a
default everywhere, wrap the component once in your own composable that passes the `UiText`.

## Right-to-left

The library is laid out with start/end padding throughout, so it mirrors correctly under an RTL
layout direction. The part that needs more than mirroring is numbers.

A dial code is a `+` followed by digits. Under the Unicode bidirectional algorithm `+` is *neutral*
and digits are *weak*, so `+254` has no direction of its own and takes the direction of the
paragraph around it. Inside Arabic or Hebrew UI that paragraph runs right-to-left, and the `+` is
resolved to the right of the digits — `+254` renders as `254+`. The same applies to a grouped
national number, where the groups themselves can reorder.

Phone numbers are written left-to-right in every locale, RTL ones included. The library pins them:

- Whole-composable numbers — the dial code in the phone prefix, the `KE · +254` metadata line, the
  number in the phone field — get `textDirection = Ltr`, which fixes the content without touching
  where the composable sits in its parent. A right-aligned field stays right-aligned.
- Numbers *inside* a translated sentence — the `+254` in "Enter a valid Kenya number — 9 digits
  after +254" — are wrapped in Unicode isolate characters instead, so the number is LTR while the
  sentence stays RTL.

See `CountryPickerBidi` for the details.

!!! tip "Testing RTL"
    The bug reproduces with English text in an RTL *layout*, so you do not need Arabic strings to
    see it. Wrap a preview:

    ```kotlin
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        PhoneNumberField(onValueChange = {})
    }
    ```

    `PhoneInputRefinementPreviews.kt` has RTL previews for the field and the sheet.

## Adding a locale

1. Copy `ccp/src/commonMain/composeResources/values/strings.xml` to `values-<code>/strings.xml`
   in the same directory.
2. Translate every entry. Keep the format specifiers (`%1$s`, `%2$d`) intact — reordering arguments
   is allowed, that is what the positional form is for. Compose resources only substitute the
   *positional* form, so never write a bare `%s` or `%d`, and write apostrophes and quotes
   unescaped (`you're`, not `you\'re`).
3. Give each `<plurals>` the categories your language uses. Getting this wrong is silent: a missing
   category falls back to `other` and reads wrongly rather than failing.
4. Run `./gradlew :ccp:verifyTranslationParity`, which fails if the locale is missing any string.
