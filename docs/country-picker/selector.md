# Country selector

`CountrySelector` is the library's primary entry point: a tappable surface showing the current
selection that opens the country sheet. It works for any single-country field.

```kotlin
var country by rememberSaveable { mutableStateOf<Country?>(null) }

CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    label = UiText.of("Country of residence"),
)
```

## Variants

`variant` switches the footprint. Every variant opens the same sheet with the same `config` and
produces the same callback; colors, flags and shapes come from the [style](../theming.md).

| `CountrySelectorVariant` | Looks like | Notes |
|---|---|---|
| `Elevated` (default) | A raised card-like field with a soft layered shadow | The Signature look |
| `Outlined` | A bordered field on the page's own background | |
| `Filled` | A tonal field with no border | |
| `Underlined` | A single rule under the content | For dense forms |
| `Card` | A large card: label, the name in large type, and region · dial code · ISO, with a "Change" action | For settings and review screens |
| `Compact` | <span class="pill">🇩🇪 Germany ˅</span> | Pill with flag and name. `label` is not drawn |
| `FlagOnly` | <span class="pill">🇩🇪 ˅</span> | Pill with the flag alone. Carries its own content description |
| `DialCode` | <span class="pill">🇰🇪 +254 ˅</span> | Pill with flag and dial code |

Every full-width variant grows a soft accent ring while focused or open, thickens its border, and
shakes once on entering the error state. Pressing scales the field down slightly.

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    variant = CountrySelectorVariant.Compact,
    label = null,
)
```

The `FlagOnly` variant has no visible text, so it announces itself as
*"Selected country: Germany. Double tap to change."* with no extra wiring.

For the phone prefix you will rarely call the `DialCode` variant directly:
[`PhoneNumberField`](../phone/field.md) already embeds it, and
[`PhoneCountryCodeSelector`](../phone/field.md#standalone-dial-code-selector) wraps it with the
right phone configuration.

## Label, placeholder and helper text

All user-facing strings are [`UiText`](#uitext) values, so you can pass a literal or a resource.

| Parameter | Default | Purpose |
|---|---|---|
| `label` | "Country of residence" | Field label. Ignored by the pill variants |
| `placeholder` | "Select country" | Shown while `selectedCountry` is `null` |
| `supportingText` | `null` | Helper text under the field |
| `errorText` | `null` | Shown when `state` is `Error`; replaces `supportingText` |
| `successText` | `null` | Shown when `state` is `Success`; replaces `supportingText` |
| `required` | `false` | Marks the field required in its label and to accessibility services |
| `sheetTitle` / `sheetSubtitle` | "Select country" / subtitle | The sheet header |

## Interaction states

`state` is a single `CountrySelectorState` rather than several booleans, because the states are
mutually exclusive.

| `CountrySelectorState` | Behaviour |
|---|---|
| `Default` | Normal, interactive |
| `Disabled` | Not interactive and visibly inert. `enabled = false` is a shortcut for this |
| `ReadOnly` | Shows a value that cannot be changed. Distinct from `Disabled`, which implies unavailability |
| `Loading` | Shows a spinner in the trailing slot, e.g. during country detection |
| `Error` | Error stroke, icon and `errorText` |
| `Success` | Success stroke and `successText` |

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    state = if (showError) CountrySelectorState.Error else CountrySelectorState.Default,
    errorText = UiText.of("Please choose your country of residence"),
    required = true,
)
```

Error and success override the resting stroke on every variant that has one, so validation is
visible on a compact pill as well as on a full field. Focus and error thicken the border as well as
recolouring it, so the state is never conveyed by hue alone.

## What the field itself shows

`config` controls the **sheet**. Two parameters control what the **field** displays:

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    labelMode = InputLabelMode.Hidden,                        // no visible label
    supportingContent = CountrySupportingContent.DialCode,    // "+49" under the name
)
```

| Parameter | Default | Meaning |
|---|---|---|
| `labelMode` | `Floating` | `Floating` draws the label above the value; `Hidden` removes it visually only. The label is still exposed to accessibility |
| `supportingContent` | `None` | `None`, `IsoCode` ("DE"), `DialCode` ("+49") or `IsoAndDialCode` ("DE · +49") under the name |

The flag's frame and artwork come from the style's [`flagStyle` and `flagSource`](../theming.md#flags);
`flagStyle = CountryFlagStyle.Hidden` removes it. `trailingContent` replaces the chevron.

## Slots

The composable slots let you replace parts of the rendering while keeping the click and
accessibility behaviour.

| Slot | Replaces |
|---|---|
| `flagContent: (Country) -> Unit` | The flag renderer, e.g. to draw vector assets or a remote image |
| `trailingContent: () -> Unit` | The trailing chevron / spinner / state icon |
| `selectorContent: RowScope.(Country?) -> Unit` | The selector's entire inner layout. The escape hatch for a bespoke design; prefer `variant` first |
| `listItemContent: (CountryListItemScope) -> Unit` | The sheet's row renderer; see [custom rows](../theming.md#custom-rows) |
| `emptyContent: (String) -> Unit` | The sheet's no-results state; receives the query |

## `UiText`

Every string in the library flows through `UiText`, which keeps the components free of hardcoded
English while letting you override any title, subtitle or message at the call site.

```kotlin
UiText.of("Nationality")                            // literal
UiText.resource(Res.string.pick_market)             // Compose Multiplatform string resource
UiText.resource(Res.string.hello_name, "Ada")       // with format arguments
UiText.plural(Res.plurals.countries_selected, 3)    // plurals resource

val text: String = uiText.resolve()                 // inside composition
```

`Res` is your own module's generated Compose resources class. Android-only apps can keep passing
classic resource ids — `UiText.resource(R.string.pick_market)`, `UiText.plural(R.plurals.x, 3)` —
through Android overloads (import `com.ezzy.ccp.countrypicker.model.resource` / `plural`). Anything
else can be bridged by subclassing `UiText.Custom`.

## Data, recents and detection

These parameters are shared by every selector and covered in their own pages:

- `config`: [Configuration](configuration.md)
- `recentCountryStore`: [Recently selected countries](data.md#recently-selected-countries)
- `detector` and `detectionBehavior`: [Country detection](detection.md)
- `repository`: [Custom data sources](data.md#custom-data-sources)
- `style`: [Theming](../theming.md)

## Using the sheet on its own

A host that already manages sheet visibility, for example inside its own navigation graph, can use
the pieces directly.

`CountryPickerSheet` is the bottom sheet, driven by a `CountryPickerState`:

```kotlin
val pickerState = rememberCountryPickerState(
    config = CountryPickerDefaults.config(),
    selectedCountries = setOfNotNull(country),
)

Button(onClick = { pickerState.open() }) { Text("Choose country") }

if (pickerState.isSheetOpen) {
    CountryPickerSheet(
        state = pickerState,
        onCountrySelected = { country = it },
        onDismiss = { pickerState.dismiss() },
    )
}
```

`CountrySelectorField` is the selector surface alone, without a sheet attached, for hosts that need
the field but supply their own picker.

### `rememberCountryPickerState`

```kotlin
fun rememberCountryPickerState(
    config: CountryPickerConfig = CountryPickerConfig(),
    selectedCountries: Set<Country> = emptySet(),
    repository: CountryRepository = CountryRepository.Default,
    recentCountryStore: RecentCountryStore = NoOpRecentCountryStore,
    detector: CountryDetector? = null,
    detectionBehavior: CountryDetectionBehavior = CountryDetectionBehavior.ShowBadge,
    onDetectedCountry: (Country) -> Unit = {},
): CountryPickerState
```

The sheet's transient state (query, region, pending selection, open/closed) survives rotation and
process death. Only ISO codes are saved, never `Country` objects. Changing `config` updates the
existing state rather than recreating it, so toggling configuration mid-session does not wipe the
user's search.

### `CountryPickerState` at a glance

| Member | Purpose |
|---|---|
| `isSheetOpen`, `open()`, `dismiss()` | Sheet visibility. `dismiss()` discards any pending selection and is the single exit for the close icon, scrim, drag-down and system back |
| `searchQuery`, `updateSearchQuery()`, `clearSearch()`, `isSearching` | Search. Filtering is synchronous; clearing restores the region filter that was active before searching |
| `selectedRegion`, `selectRegion()`, `availableRegions` | Region filter, `null` for "All" |
| `pendingSelection`, `toggleCountry()`, `resetPendingSelection()`, `confirmSelection()` | The staged selection. `confirmSelection()` returns the set, or `null` when the min/max bounds are not met |
| `confirmedSelection`, `hasExplicitSelection`, `markExplicitSelection()` | Your confirmed value, mirrored for rendering. `hasExplicitSelection` is what stops detection from overriding a user's choice |
| `sections`, `matches`, `resultCount` | The grouped, deduplicated rows to render |
| `quickPicks`, `quickPicksInList` | Recent, suggested and detected countries for the carousel, and whether they also appear as list sections |
| `regionCounts` | The number of countries in each region, for the filter's counts |
| `searchSuggestions` | "Did you mean" countries when a search finds nothing — close misspellings, transpositions included |
| `loadState`, `allCountries` | `Loading`, `Loaded` or `Error(message, offline)`. Only a custom data source can be anything but `Loaded` |
| `detectionResult`, `detectionSource` | The latest detection outcome |
| `feedbackMessage`, `consumeFeedback()` | Transient notices such as "you can select at most 5 countries" |
| `isEmptyByConfiguration`, `isEmptyBySearch`, `canConfirm` | Derived flags for the empty states and the Confirm button |
