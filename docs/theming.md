# Theming

Every UI entry point takes its colors, shapes, dimensions, typography and motion from
`CountryPickerDefaults`. Every default reads from `MaterialTheme.colorScheme` and
`MaterialTheme.typography`, so the picker follows your app's light and dark theme, including dynamic
color, with no configuration at all. Override only what actually needs to differ.

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    colors = CountryPickerDefaults.colors(selectorContainer = MyBrand.fieldBackground),
    shapes = CountryPickerDefaults.shapes(selectorOutlined = RoundedCornerShape(4.dp)),
)
```

Each of `colors`, `shapes`, `dimensions`, `typography` and `motion` is an `@Immutable` data class, so
you can also build one once, `copy()` it, and pass the same value to every selector in the app.

## Colors

`CountryPickerDefaults.colors(…)` returns a `CountryPickerColors`. The names are **roles**, not raw
values: `selectedRowContainer` rather than "light purple", so retheming changes meaning instead of
guessing which of twelve purples to override. No composable in the library reads `colorScheme`
directly; they all go through this class.

| Group | Roles | Default source |
|---|---|---|
| Selector | `selectorContainer`, `selectorContent`, `selectorLabel`, `selectorSecondaryContent`, `selectorBorder`, `selectorFocusedBorder`, `selectorDisabledContainer`, `selectorDisabledContent`, `chevron` | `surfaceContainerHighest`, `onSurface`, `onSurfaceVariant`, `outline`, `primary`, … |
| Validation | `error`, `success` | `error`; `success` is the library's own token, see below |
| Sheet | `sheetContainer`, `sheetContent`, `sheetSecondaryContent`, `dragHandle`, `scrim` | `surfaceContainerLow`, `onSurface`, `onSurfaceVariant`, `outlineVariant`, `scrim` at 32% |
| Search | `searchContainer`, `searchFocusedBorder`, `searchContent`, `searchPlaceholder`, `searchHighlight` | `surfaceContainerHigh`, `primary`, `onSurface`, `onSurfaceVariant`, `primary` at 28% |
| Rows | `sectionLabel`, `rowContainer`, `rowContent`, `rowSecondaryContent`, `rowDisabledContent`, `selectedRowContainer`, `selectedRowContent`, `checkIcon`, `checkboxChecked`, `checkboxUnchecked` | `primary`, transparent, `onSurface`, `secondaryContainer`, `onSecondaryContainer`, … |
| Cards and chips | `currentSelectionContainer`, `detectedBadgeContainer`, `detectedBadgeContent`, `regionChipContainer`, `regionChipSelectedContainer`, `regionChipContent`, `regionChipSelectedContent`, `regionChipBorder` | `surfaceContainerHigh`, `secondaryContainer`, `onSecondaryContainer`, transparent, `outlineVariant` |
| Flag fallback | `flagPlaceholderContainer`, `flagPlaceholderContent` | `surfaceContainerHigh`, `onSurfaceVariant` |

### The success color

Material 3 has no success role, and reusing `tertiary` for "residency confirmed" would break for any
host whose tertiary is red-ish. So the library defines exactly one raw color pair,
`CountryPickerTokens.SuccessLight` and `SuccessDark`, both with ≥ 4.5:1 contrast on the M3 baseline
surfaces, picked automatically by `isSystemInDarkTheme()`. If your design system has a real success
color, pass it: `CountryPickerDefaults.colors(success = MyBrand.success)`.

## Shapes

```kotlin
CountryPickerDefaults.shapes(
    selectorFilled = null,     // rounded top, near-square bottom (M3 filled text field)
    selectorOutlined = null,   // RoundedCornerShape(12.dp)
    sheet = null,              // top corners 28.dp
    searchField = null,        // fully rounded, M3 search-bar style
    row = null,                // square: rows are edge-to-edge
)
```

The factory takes nullable overrides so you can change one corner without restating the rest; the
full `CountryPickerShapes` also has `selectorMinimal`, `selectorPill`, `currentSelectionCard`,
`regionChip`, `detectedBadge`, `flagCircle`, `flagRounded`, `flagSquare` and `button`, reachable via
`copy()`. Shapes are not decorative: every clickable component clips its ripple to the shape declared
here, so a wrong shape shows up immediately as a ripple bleeding past a corner.

## Dimensions

`CountryPickerDefaults.dimensions()` returns a `CountryPickerDimensions` with every size and spacing
the picker uses: selector heights per variant, paddings, flag sizes, chevron and check sizes, row and
search field heights, chip sizes, and `minimumTouchTarget = 48.dp`.

The 48dp touch target is a **hard floor**, not a default. The compact variants reach it through
padding around smaller visuals rather than by shrinking the target. Focus and error thicken the
selector border (`selectorBorderWidth` 1dp → `selectorFocusedBorderWidth` 2dp) so state is never
conveyed by color alone.

## Typography

`CountryPickerDefaults.typography()` derives every style from `MaterialTheme.typography`, so the
picker inherits your type scale, font family and the user's font-size preference. The one style built
by hand is `sectionHeader`: an uppercase, wide-tracked `labelMedium` for the SELECTED / SUGGESTED / ALL
COUNTRIES headers, which Material has no role for.

Styles: `sheetTitle`, `sheetSubtitle`, `fieldLabel`, `selectorValue`, `selectorSecondary`,
`compactValue`, `dialCodeValue`, `countryName`, `countryMetadata`, `sectionHeader`, `resultCount`,
`buttonLabel`, `helperText`, `badgeLabel`, `regionChipLabel`, `searchInput`.

## Motion

`CountryPickerDefaults.motion()` returns a `CountryPickerMotion` with the picker's animation specs in
one place, grouped by what the motion communicates:

| Group | Duration | Used for |
|---|---|---|
| `micro` | 140 ms | State flips the user expects: chevron rotation, checkbox, color and border changes |
| `content` | 240 ms | Something changed on screen: list swaps, count changes, section resizes |
| `selectionSpring` | low-bounce spring | Selection feedback |

Sheet motion is deliberately absent: `ModalBottomSheet` owns its own entrance and exit.

**Reduced motion is respected by default.** `motion()` reads the platform animator duration scale and
collapses every duration to near zero when the user has turned animations off in Developer options or
accessibility settings. Nothing in the library conveys information *only* through movement. Pass
`motion(respectSystemAnimationScale = false)` only if your app already applies its own reduction, or
call `motion.withMotionEnabled(false)` to disable animation on a particular screen.

## Flags

Flags are the platform's emoji glyphs, so the library ships no bitmaps. When a country has no emoji
sequence (Kosovo is the real case), an ISO-code badge is drawn instead of an empty box.

Two independent axes describe how a flag is drawn:

| `CountryFlagShape` (the mask) | `CountryFlagStyle` (the fill) |
|---|---|
| `Circle` (default in the sheet), `Rounded`, `Square`, `Original` (true 4:3, no crop), `Hidden` | `Plain` (no background, natural aspect ratio), `FilledContainer` (flag scaled edge-to-edge inside the mask), `TonalContainer` (flag on a soft theme-derived background) |

`CountryFlagConfig(style, shape, size, containerSize, contentPadding)` bundles them for the
components that accept a `flagConfig`, such as `CountrySelectorContentConfig` and
`PhoneNumberInputStyle`. The sheet's rows use `CountryPickerConfig.flagShape` and `rowFlagStyle`.

To draw flags yourself, for example from the bundled `EzzyIcons` vectors or a remote image, pass
`flagContent` to any selector, sheet or list:

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    flagContent = { c -> AsyncImage(model = "https://flags.example/${c.iso2Code}.png", contentDescription = null) },
)
```

The `CountryFlag` composable is public too, for use in your own rows or summaries.

## Custom rows

`listItemContent` replaces the sheet's row renderer. It receives a `CountryListItemScope` with
everything a row needs, so you never reimplement toggle logic:

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    listItemContent = { scope ->
        MyCountryRow(
            country = scope.country,
            highlight = scope.match,          // search highlight range
            selected = scope.selected,
            enabled = scope.enabled,
            multiSelect = scope.selectionMode == CountrySelectionMode.Multiple,
            onClick = scope.onClick,          // call this; do not toggle state yourself
        )
    },
)
```

The default row, `CountryListItem`, is also public if you only want to wrap or decorate it. It has a
single click target (the checkbox is drawn but not independently clickable, which avoids
double-toggling and duplicate TalkBack nodes), clips its ripple to the row shape, and marks selection
with a tinted background **and** a check mark or ticked box so the state survives greyscale and
high-contrast modes.

## Building blocks

All of the sheet's parts are public composables that take the same `colors` / `shapes` /
`dimensions` / `typography` / `motion` parameters, for hosts assembling their own picker layout:
`CountryPickerSheet`, `CountryList`, `CountryListItem`, `CountrySectionHeader`, `CountrySearchField`,
`CountryRegionFilters`, `CountryFlag`, `DetectedCountryBadge`, `DetectedCountrySuggestion`, and the
sheet states `CountrySearchEmptyState`, `CountryErrorState`, `CountryNoneAvailableState` and
`CountryLoadingState`.
