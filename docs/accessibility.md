# Accessibility

Accessibility is built into the components rather than left to the host. These are the guarantees
the library makes.

## Touch targets

Every clickable element is at least **48dp**, via `CountryPickerDimensions.minimumTouchTarget`. The
compact and flag-only pills are visually 40–48dp but padded out to the floor. The whole selector
surface is the click target, not just the chevron; a dropdown whose arrow is the only hit area is a
recurring accessibility failure.

The one deliberate exception is `PhoneFieldSize.Compact` / `ExtraCompact`, which exist specifically
so the phone field can be shorter than 48dp. Use them where the layout justifies it.

## Labels and descriptions

- Selectors expose **one complete sentence** to TalkBack rather than fragments: *"Country of
  residence, Kenya"* instead of three separate nodes. The `FlagOnly` variant, which has no visible
  text, announces *"Selected country: Germany. Double tap to change."*
- Hiding a label visually (`InputLabelMode.Hidden`) never removes it from accessibility. The phone
  field's `accessibilityLabel` defaults to the label and is exposed regardless of whether the label
  renders.
- The phone prefix spells its dial code digit by digit, *"calling code plus two five four"*, so a
  screen reader does not read `+254` as "two hundred fifty-four".
- `required` marks a field required both in its label and to accessibility services.
- Flags are decorative and contribute nothing to the accessibility tree. TalkBack reads "Kenya", not
  "flag of Kenya, Kenya".
- Rows announce name and metadata as one phrase; selection state goes through the platform's
  `selected` and `stateDescription` semantics so it is announced in the user's language and in the
  position their screen reader expects.
- Multi-selection summaries ("3 countries selected") are announced exactly as displayed, not as the
  first selected country.

## Not by color alone

- Focus and error **thicken** the border as well as recolouring it.
- A selected row gets a tinted background **and** a check mark or ticked box.
- Search highlights use a background tint **and** a weight bump on the same text run, with no
  characters inserted, so the highlight survives greyscale and text selection still works.
- Region chips report `Role.Tab` and their `selected` state, so TalkBack says "Africa, selected".

## Structure and live regions

- Section headers carry heading semantics, so screen-reader users can jump between Selected,
  Suggested and All countries with heading navigation.
- The search result count is a polite live region: it is mentioned when it settles, without
  interrupting mid-keystroke.
- The "no results" state is an assertive live region that echoes the query back, so the user knows
  whether the typo is theirs.
- The row is the single click target. The multi-select checkbox is drawn but not independently
  focusable, which avoids the classic double-toggle and duplicate-node problems of a `Checkbox` inside
  a clickable row.

## Reduced motion

`CountryPickerDefaults.motion()` reads the system animator duration scale and disables animation when
the user has turned it off. State remains fully legible without motion; nothing is conveyed only
through movement. See [Motion](theming.md#motion).

## Keyboard

- The search field's IME action is Search and dismisses the keyboard, revealing the results that are
  already filtered live.
- The phone field's `onDone` fires only when the number is valid, so a Done tap never submits garbage.
- Search does not auto-focus when the sheet opens, because a keyboard covering most of the list is
  hostile to users who came to browse.
