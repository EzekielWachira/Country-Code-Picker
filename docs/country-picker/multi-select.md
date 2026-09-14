# Multi-selection

`MultiCountrySelector` chooses several countries: supported markets, service coverage, travel
destinations, the countries a payment method works in.

```kotlin
var markets by rememberSaveable { mutableStateOf(emptySet<Country>()) }

MultiCountrySelector(
    selectedCountries = markets,
    onSelectionConfirmed = { markets = it },   // fires only when the user taps Confirm
    label = UiText.of("Markets"),
    config = CountryPickerDefaults.multiSelectConfig(
        minimumSelectionCount = 1,
        maximumSelectionCount = 5,
    ),
)
```

## Pending versus confirmed

The confirmed selection is **never mutated in place**. Ticking rows edits a *pending* set inside the
picker state. Only Confirm promotes it and calls `onSelectionConfirmed`; Cancel, the scrim, a
drag-down and system back all discard it; Reset clears it. That is the difference between a Cancel
button that works and one that merely looks like it does.

Opening the sheet seeds the pending set from the confirmed one, so the user starts from where they
are and cancelling returns them there.

## Bounds

| Setting | Effect |
|---|---|
| `minimumSelectionCount` | Confirm stays disabled below this count. Defaults to `1` in `multiSelectConfig()` |
| `maximumSelectionCount` | Taps past the ceiling are refused **with a visible notice** ("you can select at most 5 countries") rather than silently ignored. `null` means unlimited |
| `autoConfirmOnMaximum` | Confirm automatically when the maximum is reached. Reasonable for `max = 1`; surprising above that, so it defaults to `false` |

## The selector's value line

By default the field reads "3 countries selected". Override it with `summaryText`, for example to
list the names:

```kotlin
MultiCountrySelector(
    selectedCountries = markets,
    onSelectionConfirmed = { markets = it },
    summaryText = UiText.of(markets.joinToString { it.displayName }),
)
```

The accessibility description follows the summary, so a screen reader announces the same thing a
sighted user sees rather than only the first selected country.

## Everything else

`MultiCountrySelector` accepts the same `variant`, `state`, `label`, `placeholder`,
`supportingText`, `errorText`, `required`, sheet title, `recentCountryStore`, `repository`, theming
and slot parameters as [`CountrySelector`](selector.md). Recents are recorded for every confirmed
country.

For a host driving the sheet directly, `MultiCountryPickerSheet` is a thin wrapper over
`CountryPickerSheet` that takes `onSelectionConfirmed` instead of `onCountrySelected`:

```kotlin
val state = rememberCountryPickerState(
    config = CountryPickerDefaults.multiSelectConfig(),
    selectedCountries = markets,
)

if (state.isSheetOpen) {
    MultiCountryPickerSheet(
        state = state,
        onSelectionConfirmed = { markets = it },
        onDismiss = { state.dismiss() },
    )
}
```
