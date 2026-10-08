# Configuration

`CountryPickerConfig` describes *how* a picker behaves, in one immutable value. It exists instead of
two dozen boolean parameters on `CountrySelector`: a config value can be named, reused across every
selector that should behave identically, and built up by your own defaults layer. How the picker
*looks* is the style's job — see [Theming](../theming.md).

```kotlin
val marketPicker = CountryPickerDefaults.config(
    allowedCountryCodes = supportedMarkets,
    suggestedCountryCodes = listOf("US", "GB"),
)

CountrySelector(selectedCountry = a, onCountrySelected = { a = it }, config = marketPicker)
CountrySelector(selectedCountry = b, onCountrySelected = { b = it }, config = marketPicker)
```

## Factories

`CountryPickerDefaults` ships three starting points. Each returns a `CountryPickerConfig`, so you can
`copy()` it for anything the factory does not expose.

=== "config()"

    Single selection with search and dismiss-on-select.

    ```kotlin
    CountryPickerDefaults.config(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        disabledCountryCodes: Set<String> = emptySet(),
        suggestedCountryCodes: List<String> = emptyList(),
        showSearch: Boolean = true,
    )
    ```

=== "multiSelectConfig()"

    Multiple selection: the sheet stays open after each tick and nothing reaches the caller until
    Confirm. See [Multi-selection](multi-select.md).

    ```kotlin
    CountryPickerDefaults.multiSelectConfig(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        disabledCountryCodes: Set<String> = emptySet(),
        minimumSelectionCount: Int = 1,
        maximumSelectionCount: Int? = null,
        suggestedCountryCodes: List<String> = emptyList(),
    )
    ```

=== "phoneConfig()"

    The phone country-code picker. `PhoneNumberField` and `PhoneCountryCodeSelector` use it by
    default, and always show dial codes on its rows, whatever the theme says — in that picker the dial
    code is the thing being chosen.

    ```kotlin
    CountryPickerDefaults.phoneConfig(
        allowedCountryCodes: Set<String>? = null,
        excludedCountryCodes: Set<String> = emptySet(),
        suggestedCountryCodes: List<String> = emptyList(),
    )
    ```

## Allowed, excluded and disabled countries

```kotlin
CountryPickerDefaults.config(
    allowedCountryCodes = setOf("US", "CA", "GB", "KE"),   // null (default) = every country
    excludedCountryCodes = setOf("KP", "IR"),               // exclusion always wins
)
```

- `allowedCountryCodes = null` offers every country. An **empty set is meaningful**: it allows nothing,
  and the sheet shows its own "no countries available" state rather than silently falling back to
  everything.
- `excludedCountryCodes` is applied after the allow list, so exclusion always wins.
- `disabledCountryCodes` shows countries but makes them unselectable, with a "Not available" tag on
  the row. Prefer this over exclusion when the user needs to understand *why* their country is missing.

ISO codes are matched case-insensitively, and `UK` is normalized to `GB`.

## Sections

The list is grouped into sections in a fixed priority order. **A country appears in the first section
that claims it and nowhere else**, so a selected Germany never also shows up under Suggested or All
countries, and the check marks are never ambiguous.

| `CountrySectionKind` | Contents | Controlled by |
|---|---|---|
| `Selected` | The current selection (single) or the pending selection (multi) | `showCurrentSelection` for the card above the list |
| `Recent` | Most recent first, from the `RecentCountryStore` | `showRecentlySelected`, `recentCountryLimit`, and passing a store |
| `Suggested` | From `suggestedCountryCodes`, in the order given | `showSuggestedCountries`, `suggestedCountryCodes` |
| `All` | Everything else, alphabetical or by `countryComparator` | — |
| `SearchResults` | Replaces all of the above while searching: one ranked, untitled list | `showResultCount` |

```kotlin
CountryPickerDefaults.config(
    suggestedCountryCodes = listOf("US", "GB", "KE"),
)

CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    config = config,
    recentCountryStore = rememberDefaultRecentCountryStore(),  // omit to persist nothing
)
```

`suggestedCountryCodes` is **deliberately empty by default**. Which countries deserve promotion is a
product decision, and a library that ships `["US", "GB"]` as a universal default is asserting
something false about most apps. Recents are covered in
[Data, search and recents](data.md#recently-selected-countries).

## Region filters

```kotlin
CountryPickerDefaults.config().copy(
    enabledRegions = setOf(CountryRegion.Africa, CountryRegion.Europe),
    initialRegion = CountryRegion.Africa,
)
```

The sliding filter offers All, Africa, Americas, Asia, Europe and Oceania, each with its count (`CountryRegion`, following the
UN M49 top-level groupings). Every country maps to exactly one region, so filtering partitions the
dataset without gaps or overlaps. Region filtering happens *before* search, so the result count reads
"12 results in Africa" rather than 12 results globally. Entering a search hides the filter; clearing
the search restores the region that was active.

## Search

Search is local, synchronous and undebounced: the country fields are normalized once when the dataset
is built, so filtering the full list on every keystroke is sub-millisecond.

```kotlin
CountryPickerConfig(
    searchFields = setOf(CountrySearchField.Name, CountrySearchField.Iso2, CountrySearchField.Iso3),
    minimumSearchQueryLength = 1,
    highlightSearchMatches = true,
    showResultCount = true,
)
```

| `CountrySearchField` | Matches |
|---|---|
| `Name` | `displayName`, accent-folded. The only field that produces a highlight range |
| `Iso2` | ISO alpha-2, prefix match (`"ke"` → Kenya) |
| `Iso3` | ISO alpha-3, prefix match (`"ken"` → Kenya) |
| `DialCode` | Dial code with or without `+` (`"254"` and `"+254"` → Kenya) |
| `AlternativeNames` | Aliases such as `"uae"`, `"holland"`, `"deutschland"`, `"czech republic"` |

Omitting `DialCode` makes a purely numeric query match nothing, which is what a residence picker
wants. Results are ranked by match quality, then by name; see
[Ranked search](data.md#ranked-search) for the tiers.

## Alphabet index

236 countries is roughly fifteen screens. Search covers the user who knows the name; the A–Z rail
covers the one who is browsing, or knows only roughly where a country falls.

The rail is part of the style rather than the config, and **on by default**. Turn it off with:

```kotlin
val style = CountryPickerStyles.signature()
CountryPickerTheme(style.copy(layout = style.layout.copy(showAlphabetIndex = false))) { … }
```

Tap a letter to jump, or drag along the rail to scrub, with a magnified bubble following your finger
and a light haptic on each letter change. The rail only offers letters that lead somewhere, and it
hides itself while a search is narrowing the list — it indexes the alphabetical list, which is not
what is on screen then. The list keeps its rows and carousel clear of the rail while it is shown.

It is hidden from the accessibility tree. A screen-reader user already
reaches every row by swiping through the list, and putting 26 unlabelled single-character targets in
front of that list makes it harder to use, not easier; search remains the accessible fast path.

### Building your own

If you render `CountryList` yourself, the same machinery is public. The piece worth reusing is the
index: `CountryList` flattens sections *and their sticky headers* into one `LazyColumn`, so the
*n*th country is not at lazy index *n*, and doing that arithmetic per call site is how off-by-one
scroll bugs get written.

```kotlin
val index = rememberCountryListIndex(state.sections.value)
val scope = rememberCoroutineScope()

Row {
    CountryList(state = state, onCountryClick = …, listState = listState, modifier = Modifier.weight(1f))
    CountryIndexRail(
        letters = index.letters,
        onLetterSelected = { letter ->
            index.lazyIndexOf(letter)?.let { scope.launch { listState.scrollToItem(it) } }
        },
    )
}
```

`CountryListIndex` also resolves a specific country, which is what to use for "scroll to the
selected country when the sheet opens":

```kotlin
index.lazyIndexOf("KE")        // by ISO code
index.lazyIndexOf(country)     // by Country
```

## Reference

Every property of `CountryPickerConfig`, with its default.

| Property | Default | Meaning |
|---|---|---|
| `selectionMode` | `Single` | `Single` commits on tap; `Multiple` stages a pending selection behind Confirm |
| `allowedCountryCodes` | `null` | Only these ISO alpha-2 codes are offered. Empty set = nothing |
| `excludedCountryCodes` | `emptySet()` | Removed even if allowed |
| `disabledCountryCodes` | `emptySet()` | Shown but not selectable |
| `enabledRegions` | all five | Regions offered as chips |
| `initialRegion` | `null` | Region pre-selected when the sheet opens; `null` = All |
| `showRecentlySelected` | `true` | Recently selected countries (needs a `RecentCountryStore`) |
| `showSuggestedCountries` | `true` | Suggested countries |
| `showSearch` | `true` | The search field |
| `closeOnSingleSelection` | `true` | Dismiss the sheet after a single selection |
| `minimumSelectionCount` | `0` | Multi-select floor. Confirm stays disabled below it |
| `maximumSelectionCount` | `null` | Multi-select ceiling, or unlimited. Further taps are refused with a visible notice |
| `autoConfirmOnMaximum` | `false` | Confirm automatically once the maximum is reached |
| `searchFields` | all | Fields search matches against |
| `minimumSearchQueryLength` | `1` | Characters before search filters |
| `suggestedCountryCodes` | `emptyList()` | Codes for the Suggested section |
| `recentCountryLimit` | `5` | How many recents to display |
| `countryComparator` | `null` | Custom ordering within All countries; `null` = alphabetical by display name |

The config also exposes `isMultiSelect`, `isSelectionCountValid(count)` and `isAtMaximum(count)` for
hosts that render their own UI around the state.

How the picker *looks* — whether rows show flags, dial codes, ISO codes or region names, region
filters, the alphabet rail, search highlighting, the result count, and where recent and suggested
countries appear — is set in the style's `CountryPickerLayout`. See [Theming](../theming.md#layout).
