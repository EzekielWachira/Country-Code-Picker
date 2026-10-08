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

`variant` switches the visual form. Every variant opens the same sheet with the same `config` and
produces the same callback.

| `CountrySelectorVariant` | Looks like | Notes |
|---|---|---|
| `Outlined` (default) | Full-width outlined field with a floating label | The design's default for "Country of residence" |
| `Filled` | Full-width filled field: tonal background, rounded top, underline | Matches a Material 3 filled text field |
| `Minimal` | Full-width, underline only, no fill | For dense forms |
| `Compact` | <span class="pill">🇩🇪 Germany ˅</span> | Pill with flag and name. `label` is ignored |
| `FlagOnly` | <span class="pill">🇩🇪 ˅</span> | Pill with the flag alone. Carries its own content description |
| `DialCode` | <span class="pill">🇰🇪 +254 ˅</span> | Pill with flag and dial code. Used inside the phone field |

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

`config` controls the **sheet**. `contentConfig` independently controls what the **field** displays:
label visibility, the metadata line, the flag and the chevron.

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    contentConfig = CountrySelectorDefaults.contentConfig(
        labelMode = InputLabelMode.Hidden,                        // no visible label
        supportingContent = CountrySupportingContent.DialCode,    // "+49" under the name
        showDropdownIcon = false,
    ),
)
```

| `CountrySelectorContentConfig` | Default | Meaning |
|---|---|---|
| `labelMode` | `Floating` | `Floating` draws the label above the value; `Hidden` removes it visually only. The label is still exposed to accessibility |
| `showCountryName` | `true` | Whether the country's name renders |
| `supportingContent` | `IsoAndDialCode` | `None`, `IsoCode` ("DE"), `DialCode` ("+49") or `IsoAndDialCode` ("DE · +49") |
| `showFlag` | `true` | Whether the leading flag renders |
| `showDropdownIcon` | `true` | Whether the trailing chevron renders |
| `flagConfig` | tonal container | How the flag is drawn; see [Theming](../theming.md#flags) |

`CountrySelectorDefaults.rowOnlyContentConfig()` gives a row-only layout (no label, no metadata) for a
selector embedded in a form row that already labels it. Passing `contentConfig = null` (the default)
keeps the field's original derivation from `label` and `config`.

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
- `colors`, `shapes`, `dimensions`, `typography`, `motion`: [Theming](../theming.md)

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
| `loadState`, `allCountries` | `Loading`, `Loaded` or `Error(message, offline)`. Only a custom data source can be anything but `Loaded` |
| `detectionResult`, `detectionSource` | The latest detection outcome |
| `feedbackMessage`, `consumeFeedback()` | Transient notices such as "you can select at most 5 countries" |
| `isEmptyByConfiguration`, `isEmptyBySearch`, `canConfirm` | Derived flags for the empty states and the Confirm button |
