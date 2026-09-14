# Country detection

Detection pre-fills a likely country from device signals so most users never open the sheet.

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    detector = DefaultCountryDetector(context),
    detectionBehavior = CountryDetectionBehavior.ShowBadge,
)
```

Three rules the library relies on:

1. **No location permission.** Country-level detection never justifies a runtime permission prompt.
   `DefaultCountryDetector` reads only the SIM and network country codes and the locale, all of which
   are permission-free.
2. **Never called from composition.** `CountryDetector.detectCountry()` is `suspend` and runs from a
   `LaunchedEffect`, so a network-backed detector can take as long as it needs.
3. **Detection is a suggestion, not a command.** An explicit user selection always outranks a detected
   one. A late-arriving lookup can never silently contradict a country the user already tapped, and a
   detected code the configuration excludes is ignored rather than forced into the field.

## `DefaultCountryDetector`

```kotlin
DefaultCountryDetector(
    context: Context,              // any context; the application context is fine
    fallbackIso2Code: String? = "US",
)
```

Tries, in order of confidence: **SIM country → network country → configuration locale → default
locale → fallback**. Pass `fallbackIso2Code = null` to report `Unavailable` instead of guessing, which
is the right choice when a wrong default is worse than no default. Malformed codes some devices report
(`""`, `"--"`, three-letter codes) are validated against the dataset and dropped.

The locale is the weakest signal (plenty of people run an English (US) device in Nairobi), so it is
reported as *"Based on your device language"* rather than as a location claim.

## Behaviours

| `CountryDetectionBehavior` | What the picker does |
|---|---|
| `ShowBadge` (default) | Applies the country and shows a tonal badge naming the source: *"✓ Detected · From your SIM card · tap to change"* |
| `AskFirst` | Does **not** apply anything. Surfaces the result as a suggestion the user accepts explicitly. Right for fields with legal weight (tax residency, sanctions screening), where a silently prefilled answer the user never gave is a liability |
| `Silent` | Applies the country with no visible explanation |

The two visible treatments are also available as standalone composables for hosts composing their own
layout: `DetectedCountryBadge(source)` and `DetectedCountrySuggestion(country, source, onAccept)`.

## Result and source types

```kotlin
sealed interface CountryDetectionResult {
    data object Idle : CountryDetectionResult
    data object InProgress : CountryDetectionResult          // selector shows its Loading treatment
    data class Detected(val iso2Code: String, val source: CountryDetectionSource) : CountryDetectionResult
    data object Unavailable : CountryDetectionResult         // nothing determined; not an error
}
```

`Detected` carries an ISO code rather than a `Country`, so detectors need no access to the dataset.
The picker resolves the code against whatever data source is configured.

| `CountryDetectionSource` | Meaning |
|---|---|
| `UserSelection` | The user picked it. Never overridden |
| `SavedSelection` | Restored from a previous session |
| `ActiveProfile` | Supplied by the host from the signed-in profile |
| `Sim` | The SIM card's registered country. Strongest device signal |
| `Network` | The attached network, or a host-provided IP lookup |
| `Locale` | The device language/region. Weakest |
| `Default` | A hardcoded fallback |

Each source has a `label: UiText` used for the badge's supporting text.

## Custom detectors

`CountryDetector` is a `fun interface`, so a one-off detector is a lambda. The idiomatic way to add a
backend or IP lookup without giving up the device signals is `withFallback`:

```kotlin
val detector = CountryDetector {
    api.geoCountry()?.let { CountryDetectionResult.detected(it, CountryDetectionSource.Network) }
        ?: CountryDetectionResult.Unavailable
}.withFallback(DefaultCountryDetector(context))
```

For previews and tests, `staticCountryDetector("KE")` always reports the given code.

## Reading the result

`CountryPickerState` exposes `detectionResult` and `detectionSource`; the latter is `null` once the
user has made an explicit choice. When using `rememberCountryPickerState` directly, pass
`onDetectedCountry` to adopt the detected country into your own state. It is not called when the user
has already chosen, or when the behaviour is `AskFirst`.
