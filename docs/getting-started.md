# Getting started

## Requirements

| | |
|---|---|
| **Min SDK** | 24 (Android 7.0) |
| **UI toolkit** | Jetpack Compose with Material 3 |
| **Kotlin** | A Kotlin 2.x toolchain (the library is built with Kotlin 2.2) |
| **Transitive dependencies** | Google [libphonenumber](https://github.com/google/libphonenumber) and `kotlinx-coroutines-core`. No DataStore, no image-loading library, no `material-icons-extended`. |

Flags are rendered as the platform's emoji glyphs, so the library adds no flag bitmaps to your APK.

## Installation

### 1. Add the JitPack repository

=== "settings.gradle.kts"

    ```kotlin
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven("https://jitpack.io")
        }
    }
    ```

=== "settings.gradle"

    ```groovy
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven { url 'https://jitpack.io' }
        }
    }
    ```

### 2. Add the dependency

The latest release is shown on the JitPack badge:
[![JitPack](https://jitpack.io/v/EzekielWachira/Country-Code-Picker.svg)](https://jitpack.io/#EzekielWachira/Country-Code-Picker)

=== "build.gradle.kts"

    ```kotlin
    dependencies {
        implementation("com.github.EzekielWachira:Country-Code-Picker:v0.2.0")
    }
    ```

=== "build.gradle"

    ```groovy
    dependencies {
        implementation 'com.github.EzekielWachira:Country-Code-Picker:v0.2.0'
    }
    ```

!!! tip "Experimental Material 3 APIs"
    The library opts in to `ExperimentalMaterial3Api` and `ExperimentalFoundationApi` internally
    (for `ModalBottomSheet` and sticky headers). You do not need to add any opt-in to consume it.

## Your first country selector

```kotlin
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.UiText
import com.ezzy.ccp.countrypicker.ui.CountrySelector

@Composable
fun ResidenceField() {
    var country by rememberSaveable { mutableStateOf<Country?>(null) }

    CountrySelector(
        selectedCountry = country,
        onCountrySelected = { country = it },
        label = UiText.of("Country of residence"),
    )
}
```

`CountrySelector` is a **controlled component**: `selectedCountry` is the source of truth, the
component never mutates it, and a selection only ever arrives through `onCountrySelected`. The
callback carries the complete [`Country`](country-picker/data.md#the-country-model) (ISO alpha-2 and
alpha-3, dial code, region, flag), never just a name or a code.

!!! note "Hold the selection in `rememberSaveable` or a ViewModel"
    The sheet's transient state (query, region filter, pending selection) is saved and restored for
    you. The *confirmed* selection is yours, so keep it somewhere that survives configuration changes
    and process death.

## Your first phone number field

```kotlin
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.PhoneNumberValue
import com.ezzy.ccp.countrypicker.state.rememberPhoneNumberFieldState
import com.ezzy.ccp.countrypicker.ui.PhoneNumberField

@Composable
fun PhoneField(onChange: (PhoneNumberValue) -> Unit) {
    val kenya = remember { DefaultCountryDataSource.findByIso2("KE")!! }
    val phoneState = rememberPhoneNumberFieldState(initialCountry = kenya)

    PhoneNumberField(
        state = phoneState,
        onValueChange = onChange,
    )

    Button(
        enabled = phoneState.value.isValid,
        onClick = { submit(phoneState.value.e164Number!!) },
    ) { Text("Continue") }
}
```

`PhoneNumberValue.e164Number` comes from libphonenumber and is `null` (not an empty string) while the
number cannot be parsed. Never rebuild it by concatenating a dial code onto digits; that is wrong for
every country with a national trunk prefix (a UK mobile typed as `07400 123456` is `+447400123456`,
not `+4407400123456`).

!!! warning "Pass an initial country"
    If you omit `state`, the field starts on the first country in the dataset alphabetically, which is
    Afghanistan. Pass `rememberPhoneNumberFieldState(initialCountry = …)` with a sensible default, or
    combine it with [country detection](country-picker/detection.md).

## Where to go next

<div class="grid cards" markdown>

-   **Pick a selector variant**

    ---

    Full-width, compact pill, flag-only or dial-code.

    [:octicons-arrow-right-24: Country selector](country-picker/selector.md)

-   **Restrict, group and search**

    ---

    Allowed and excluded countries, region filters, suggested and recent sections.

    [:octicons-arrow-right-24: Configuration](country-picker/configuration.md)

-   **Match your theme**

    ---

    Colors, shapes, dimensions, typography, motion and custom rows.

    [:octicons-arrow-right-24: Theming](theming.md)

-   **Already using `PhoneNumberInput`?**

    ---

    It still works unchanged. Migrate only when you want the newer features.

    [:octicons-arrow-right-24: Migration guide](legacy/migration.md)

</div>
