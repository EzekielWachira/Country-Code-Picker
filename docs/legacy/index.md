# Legacy API

`PhoneNumberInput` in `com.ezzy.ccp.components` is the original phone-number component. It is still
fully supported and its public API is unchanged. Internally it now runs on the Country Picker
foundation, so it already benefits from the 236-country dataset, ranked search and progressive
as-you-type formatting without any change on your part.

| Page | What it covers |
|---|---|
| [PhoneNumberInput](phone-number-input.md) | Usage, state hoisting, picker styles, and the full `CCPConfig` / `CCPColors` / `PhoneState` reference |
| [Migration guide](migration.md) | Mapping old building blocks onto the new API, and the behaviour changes that came with the new foundation |

!!! tip "You do not have to migrate"
    Move to the newer API when you want things `PhoneNumberInput` does not expose: multi-selection,
    region filters, recents and suggestions, phone verification, or the unified outlined field.
