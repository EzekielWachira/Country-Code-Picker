# Theming

Everything about how the picker looks lives in one value, `CountryPickerStyle`: colors, shapes,
dimensions, typography, motion, elevation, layout and haptics. Start from a preset, refine it with
`copy`, and set it once for a screen or the whole app:

```kotlin
val brand = CountryPickerStyles.signature(accent = Color(0xFF0F766E))

CountryPickerTheme(style = brand) {
    // Every selector, sheet and phone field in here uses `brand`.
    CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
    PhoneNumberField(onValueChange = { phone = it })
}
```

Every component also takes a `style` parameter, which wins over the theme for that component alone.
With no `CountryPickerTheme` at all, components use `CountryPickerStyles.signature()`.

## Presets

| Preset | Look |
|---|---|
| `CountryPickerStyles.signature()` | The default. Inset grouped lists on a cool grey canvas, flags on soft tiles, layered shadows, a sliding region filter and a quick-pick carousel. |
| `CountryPickerStyles.material()` | Material 3: every color from the enclosing `MaterialTheme`, tonal surfaces instead of shadows, edge-to-edge rows, circular flags. |
| `CountryPickerStyles.cupertino()` | iOS: system blue, San Francisco–style type, grouped lists, bare flags, recents as sections. |
| `CountryPickerStyles.minimal()` | Monochrome, hairlines instead of shadows, plain rows, flat flags all cropped to one size. |

Every preset follows the light or dark mode of the enclosing `MaterialTheme` (judged from its surface
color, so an app-level override is respected, not just the system setting), takes the host's
primary color as its accent unless given one, and turns motion off when the system asks for reduced
motion. Each also takes a `density` — `Compact`, `Comfortable` (default) or `Spacious` — which swaps
the whole `CountryPickerDimensions` set at once; `style.withDensity(…)` does the same for any style.

## Colors

`CountryPickerColors` names **roles**, not values, so retheming changes meaning rather than guessing
which of twelve purples to override. `CountryPickerColors.signature(accent, dark)` derives the full
set from one accent color.

| Group | Roles |
|---|---|
| Accent | `accent`, `onAccent`, `accentSoft`, `onAccentSoft`, `focusRing`, `highlight` |
| Surfaces | `background` (the sheet's canvas), `surface` (fields, cards, groups), `surfaceRaised`, `surfaceSunken` (tracks, chips, wells), `scrim` |
| Lines | `hairline`, `outline` |
| Text | `textPrimary`, `textSecondary`, `textTertiary`, `textDisabled` |
| Status | `error`/`errorSoft`, `success`/`successSoft`, `warning`/`warningSoft` |
| Effects | `shadow`, `skeleton`, `skeletonShine` |

`isDark` tells components which way to tune effects such as shadow density.

## Layout

`CountryPickerLayout` holds the structural choices — the parts of the design that are about
arrangement rather than color or size.

| Property | Options |
|---|---|
| `presentation` | `Adaptive` (sheet on phones, dialog from `wideScreenBreakpoint`), `BottomSheet`, `FullScreenSheet`, `Dialog` |
| `listStyle` | `InsetGrouped` (rounded groups), `Plain` (edge to edge, pinned headers), `Cards` (each row a card) |
| `flagStyle` | `Tile`, `Circle`, `Rounded`, `Plain`, `Hidden` — see [Flags](#flags) |
| `flagSource` | `CountryFlagSource.FlagCdn(…)` or `CountryFlagSource.Emoji` — see [Flags](#flags) |
| `selectionIndicator` | `Check` (draws itself in), `Radio`, `None` |
| `headerStyle` | `Large` title and subtitle, or `Compact` |
| `quickPicks` | Recent, suggested and detected countries as a `Carousel` of tiles, as `Sections`, or `Hidden` |
| Toggles | `showRegionFilters`, `showRegionCounts`, `showDialCode`, `showIsoCode`, `showRegionName`, `showDividers`, `showAlphabetIndex`, `showResultCount`, `highlightSearchMatches`, `showSearchSuggestions` |
| Sizes | `sheetHeightFraction`, `dialogMaxWidth`, `dialogMaxHeight`, `wideScreenBreakpoint` |

Behaviour — which countries, single or multiple selection, limits — stays in `CountryPickerConfig`.
The two never overlap: the config says what the picker does, the style how it looks.

## Flags

Two independent choices shape every flag.

**Where the artwork comes from** — `flagSource`:

- `CountryFlagSource.FlagCdn(shape, format, fallbackToEmoji, baseUrl)`, the default: images from
  [flagcdn.com](https://flagcdn.com), identical on every platform and covering every country in the
  dataset, Kosovo included.
- `CountryFlagSource.Emoji`: the platform's emoji. Entirely offline, but drawn differently on Android
  and iOS and missing on some Android builds and for some territories.

flagcdn offers three shapes, `FlagImageShape`:

| Shape | What it does |
|---|---|
| `Waving` | Every flag waving on the same 4:3 canvas. Uniform, and the closest to the emoji look. |
| `OriginalSameWidth` | True proportions at a common width; heights vary. |
| `OriginalSameHeight` | True proportions at a common height; widths vary. In lists they line up on their leading edge, and none is wider than its slot. |

The original shapes keep each flag's real proportions, so by design they are not all one size. For
flags that are identical in size, use `Waving`, or the `Circle` or `Rounded` frames below.

and four formats, `FlagImageFormat`: `Png` (default), `WebP`, `Jpeg` and `Svg`. Waving flags are PNG
or WebP only — `Jpeg` and `Svg` fall back to PNG for them — and the original shapes come in all four.

```kotlin
val style = CountryPickerStyles.signature()
CountryPickerTheme(
    style.copy(
        layout = style.layout.copy(
            flagSource = CountryFlagSource.FlagCdn(
                shape = FlagImageShape.OriginalSameHeight,
                format = FlagImageFormat.Svg,
            ),
        ),
    ),
) { … }
```

Images are requested at the smallest size flagcdn serves that is sharp at the screen's density, and
cached in memory and on disk, so each flag downloads once. While an image loads — and whenever it
cannot, offline for instance — the emoji flag stands in (`fallbackToEmoji = false` shows a quiet
placeholder instead). `FlagCdn.urlFor(iso2Code, widthPx, heightPx)` returns the same address the
picker uses, for drawing the artwork elsewhere.

!!! note "Network and privacy"
    `FlagCdn` fetches from a third-party CDN, which sees the device's IP address, and the library's
    manifest declares the `INTERNET` permission for it. Apps that must not make that request should
    use `CountryFlagSource.Emoji` — and may then remove the permission with `tools:node="remove"` —
    or point `baseUrl` at a mirror they host with flagcdn's layout.

**How the flag is framed** — `flagStyle`:

| Style | Frame |
|---|---|
| `Tile` | The flag on a softly shaded rounded square — the Signature look. |
| `Circle` | Cropped to a circle. |
| `Rounded` | Flat, cropped to a 4:3 rounded rectangle — the same size for every flag. |
| `Plain` | The bare flag in a 4:3 slot. |
| `Hidden` | No flag. |

`Circle` and `Rounded` crop every flag to one size. A crop of a waving flag would show its fold and
transparent corners, so those frames always use the flat artwork, whichever shape is set.

### When the emoji cannot be drawn

The emoji path has two failure modes. Kosovo (`XK`) has no emoji flag at all; and many Android builds
— most Chinese OEM ROMs, Android TV, low-end and Wear devices — ship fonts with the flag glyphs
stripped, so `🇰🇪` draws as two boxed letters. The library probes the font once with
`Paint.hasGlyph` and, in both cases, draws the ISO code in the flag's frame instead. With `FlagCdn`,
neither case arises once the image has loaded.

### Your own artwork

`flagContent` on any component replaces the artwork entirely, and is still framed by `flagStyle`:

```kotlin
CountrySelector(
    selectedCountry = country,
    onCountrySelected = { country = it },
    flagContent = { c -> Image(painterResource(myFlags.getValue(c.iso2Code)), contentDescription = null) },
)
```

## Shapes, dimensions and typography

- **`CountryPickerShapes`** — `sheet`, `dialog`, `field`, `searchField`, `groupCornerRadius`, `row`,
  `chip`, `flagTile`, `flagRounded`, `badge`, `button`, `tile`, `floatingBar` and `pill`.
- **`CountryPickerDimensions`** — field, row, tile, chip and rail sizes and spacing, and
  `minimumTouchTarget = 48.dp`. The 48dp target is a floor, not a default: compact visuals reach it
  through padding rather than by shrinking the target.
- **`CountryPickerTypography`** — twenty roles from `title` to `indexBubble`.
  `CountryPickerTypography.signature(fontFamily)` builds the set in any typeface; dial codes, numbers
  and counts use tabular figures so they line up in columns and do not jitter as they change.

## Elevation

`CountryPickerElevation` gives each surface level a `CountryPickerShadow` — a stack of soft
`ShadowLayer`s, because real depth is a tight contact shadow under a wide ambient one. Levels:
`field`, `fieldFocused`, `searchField`, `card`, `tile`, `floatingBar`, `dialog`.
`CountryPickerElevation.signature(dark)` tunes them per mode (dark surfaces need much denser shadows
to read at all); `CountryPickerElevation.Flat` turns them all off for designs built on hairlines.

## Motion

`CountryPickerMotion` holds the springs and tweens: `press`, `selection`, `layout`, `offset`, `size`,
`dp`, `color`, `fadeIn` and `fadeOut`, plus `pressedScale`, `shakeDistance` (the error shake),
`selectionDismissDelayMillis` (the pause that lets a check mark land before the sheet closes) and the
list entrance stagger.

**Reduced motion is respected by default.** The presets call `respectingSystemAnimationScale()`, which
reads the platform's animation setting and turns motion off when the user has disabled animations.
`motion.withMotionEnabled(false)` does the same for one screen. Nothing in the library conveys
information only through movement.

## Haptics

`CountryPickerHaptics` maps the picker's key moments — a selection, a tick and an untick, the A–Z rail
crossing a letter, a region change, a refused tick at the selection limit, a field entering its
error state — to platform haptics. Set any moment to `null` to silence it, or use
`CountryPickerHaptics.Off`.

## The phone field

`PhoneNumberField` takes a `variant` (`Elevated`, `Outlined`, `Filled`, `Underlined`, `Card`) and a
`PhoneNumberInputStyle` for the options only a phone field has: a floating or hidden label, what the
country prefix shows, the prefix divider, an overall `PhoneFieldSize` (`Regular`, `Compact`,
`ExtraCompact`), and the premium details — ghost digits, the progress hairline, the number-type badge
and the valid check — each of which can be switched off. See [the phone field](phone/field.md).

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

The default row, `CountryListItem`, is public too. It has a single click target, and marks selection
with a tint **and** a check mark or ticked box, so the state survives greyscale and high-contrast
modes.

## Building blocks

All of the picker's parts are public composables that take the same `style`, for hosts assembling
their own layout: `CountryPickerPanel` (the complete picker with no container — for a full-screen
route or one pane of a two-pane layout), `CountryPickerSheet`, `MultiCountryPickerSheet`,
`CountryList`, `CountryListItem`, `CountrySectionHeader`, `CountrySearchField`, `CountryRegionFilters`,
`CountryQuickPicks`, `CountryIndexRail`, `CountryFlag`, `CountrySelectorField`, `DetectedCountryBadge`,
`DetectedCountrySuggestion`, and the sheet states `CountrySearchEmptyState`, `CountryErrorState`,
`CountryNoneAvailableState` and `CountryLoadingState`.
