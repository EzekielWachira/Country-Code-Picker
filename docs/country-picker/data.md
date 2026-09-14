# Data, search and recents

## The `Country` model

`com.ezzy.ccp.countrypicker.model.Country` is the canonical country representation for the whole
library.

```kotlin
@Immutable
data class Country(
    val iso2Code: String,          // "KE" — the stable identity
    val iso3Code: String,          // "KEN"
    val displayName: String,       // "Kenya"
    val dialCode: String,          // "+254"
    val flag: String?,             // "🇰🇪", or null for the few codes with no emoji (Kosovo)
    val region: CountryRegion,     // Africa, Americas, Asia, Europe, Oceania
    val alternativeNames: List<String> = emptyList(),  // search aliases, never shown
) {
    val dialCodeDigits: String     // "254"
}
```

**Identity is `iso2Code` and nothing else.** Dial codes are not unique: `+1` is shared by the US,
Canada and a dozen Caribbean territories, `+44` by the UK, Jersey, Guernsey and the Isle of Man. So
`equals`, `hashCode` and every list key derive from the ISO alpha-2 code alone. Instances are
`@Immutable` and safe to use as Compose keys.

## The bundled dataset

`DefaultCountryDataSource` ships every ISO 3166-1 country and territory with an international dial
code: **236 entries**, each with ISO alpha-2, alpha-3, region and search aliases. It is fully local,
parsed once on first access, and is the single source of truth for country metadata (the legacy
`countryList` is derived from it).

| Member | Purpose |
|---|---|
| `countries` | All countries, English names, sorted by name |
| `findByIso2("ke")` | Lookup by ISO alpha-2, case-insensitive. `UK` maps to `GB` |
| `findByDialCode("+1")` | Every country sharing a dial code, as a list |
| `primaryForDialCode("+1")` | The conventional owner of a shared code: `+1` → United States, `+44` → United Kingdom, `+7` → Russia |
| `localizedNames(locale)` | The dataset with names resolved through `Locale.getDisplayCountry`, re-sorted with a locale-aware collator |
| `normalizeIsoCode("uk")` | Uppercases and maps well-known non-ISO codes to their canonical form |
| `flagEmoji("KE")` | The regional-indicator emoji for a code, or `null` when none exists |

Names follow current ISO usage (`Czechia`, `Türkiye`, `Eswatini`), and former names remain searchable
as aliases. Dial codes for North American territories carry their full prefix (`+1876` for Jamaica),
so the value is directly usable for display and prefix matching.

### Localized names

The library ships no translation table. To show localized names, wrap the dataset in a data source:

```kotlin
val localized = CountryDataSource { DefaultCountryDataSource.localizedNames(Locale.getDefault()) }
val repository = remember { CountryRepository(localized) }

CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    repository = repository,
)
```

The English name is kept as an alias, so searching "Germany" still finds "Allemagne".

## Custom data sources

`CountryDataSource` is a `fun interface` with one `suspend fun load(): List<Country>`. Implement it to
serve countries from a backend "supported markets" endpoint or a cut-down test fixture:

```kotlin
val source = CountryDataSource { api.supportedMarkets().map(::toCountry) }
val repository = remember { CountryRepository(source) }
```

`CountryRepository` caches the result and applies the allow/exclude rules, so reopening the sheet,
rotating the device or retrying after an error does not re-hit a remote source. Concurrent loads are
collapsed into one. It holds no UI state and is safe to keep in a singleton or DI graph;
`CountryRepository.Default` is the shared instance over the bundled dataset.

With a remote source the sheet gains two states the bundled dataset never shows: skeleton rows while
loading, and an error state with a Retry action that calls `repository.invalidate()` and reloads.
A failure becomes `CountryLoadState.Error(message, offline)` rather than an exception escaping into
composition.

## Ranked search

`CountrySearchEngine.search(countries, query, fields, minQueryLength)` is the purely local search the
sheet uses. The query is trimmed, lowercased, accent-folded and stripped of a leading `+`, so `"+254"`
and `"254"` behave identically. Results are ordered by match quality, then by name, so the same query
always produces the same order.

| Rank | Match |
|---|---|
| 0 | Country name equals the query |
| 1 | Country name starts with the query |
| 2 | ISO alpha-2 or alpha-3 equals the query |
| 3 | Dial code equals the query |
| 4 | Country name contains the query |
| 5 | An alternative name contains the query |
| 6 | ISO alpha-2 or alpha-3 starts with the query |
| 7 | Dial code starts with the query |

The last two tiers are partial-input fallbacks so a half-typed code (`"25"`, `"ke"`) still surfaces
something without outranking a real name or exact-code match. Each result is a `CountryMatch` carrying
the country, the field that matched, the highlight range within the name, and the rank.

## Recently selected countries

`RecentCountryStore` persists the countries a user has chosen so the sheet can offer them first.
**Only ISO alpha-2 codes cross this boundary**, never search queries or anything else the user typed.
Recording a country the user picked is a preference; recording what they searched for would be
tracking, and the interface makes the two impossible to confuse.

```kotlin
interface RecentCountryStore {
    fun observeRecentCountryCodes(): Flow<List<String>>   // most recent first, deduplicated
    suspend fun recordSelection(countryCode: String)
    suspend fun clear()                                    // wire to your "clear data" affordance
}
```

| Implementation | Use |
|---|---|
| `NoOpRecentCountryStore` | The default. Remembers nothing, so the library never writes to disk unless you opt in |
| `DefaultRecentCountryStore(context, maxEntries = 5, fileName)` | `SharedPreferences`-backed with an in-memory mirror. All disk work is on `Dispatchers.IO`; reads after construction are served from memory so the Recent section renders on the first frame |
| `InMemoryRecentCountryStore(initial)` | Survives recomposition but not process death. For previews, tests, and hosts that want the section without persisting |

```kotlin
val recents = remember { DefaultRecentCountryStore(context) }

CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    recentCountryStore = recents,
)
```

`SharedPreferences` rather than DataStore is deliberate: adding `androidx.datastore` and its transitive
graph to every consumer's APK to store five two-letter strings is not a good trade. An app that already
uses DataStore can implement the interface over its own store in a dozen lines.

Hosts that must not persist anything (kiosk, guest, incognito modes) keep the default no-op store or
set `showRecentlySelected = false`.

## Regions

`CountryRegion` has five values (`Africa`, `Americas`, `Asia`, `Europe`, `Oceania`), each with a
stable, locale-independent `key` that is safe to persist, and `CountryRegion.fromKey(key)` to resolve
one back.

## Converting to and from the legacy model

`com.ezzy.ccp.countrypicker.model` includes conversions so the legacy `PhoneNumberInput` API can run on
the new foundation. They are public in case you need them while migrating:

| Function | Direction |
|---|---|
| `LegacyCountry.toCanonical(): Country?` | Legacy `com.ezzy.ccp.model.Country` → canonical, matched on ISO alpha-2 |
| `Country.toLegacy(): LegacyCountry` | Canonical → legacy (lossy: alpha-3, region and aliases are dropped) |
| `Country.toSelectedCountry(): SelectedCountry` | Canonical → the legacy `SelectedCountry` used by `Phone` |
| `PhoneNumberValue.toLegacyPhone(): Phone` | New phone value → legacy `Phone` snapshot |
| `resolveCountryByCodeOrName("Kenya")` | ISO alpha-2, alpha-3, exact name or alias → `Country?`, the way the legacy `setCountry` parameter worked |
