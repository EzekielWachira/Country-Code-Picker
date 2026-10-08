# Getting started

## Requirements

| | |
|---|---|
| **Platforms** | Android (min SDK 24, Android 7.0) and iOS (`iosArm64`, `iosSimulatorArm64`) |
| **UI toolkit** | Compose Multiplatform with Material 3 — or Jetpack Compose in an Android-only app |
| **Kotlin** | A Kotlin 2.x toolchain (the library is built with Kotlin 2.4) |
| **Transitive dependencies** | Google [libphonenumber](https://github.com/google/libphonenumber) on Android and `kotlinx-coroutines-core`. On iOS the phone-number engine, [libPhoneNumber-iOS](https://github.com/iziz/libPhoneNumber-iOS), is compiled into the library. No DataStore, no image-loading library, no `material-icons-extended`. |

Flags are rendered as the platform's emoji glyphs, so the library adds no flag bitmaps to your app.

## Installation

The library is on Maven Central, which most projects already list in `settings.gradle.kts`.

=== "Kotlin Multiplatform"

    ```kotlin
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("io.github.ezekielwachira:ccp:<LATEST_VERSION>")
            }
        }
    }
    ```

=== "Android (build.gradle.kts)"

    ```kotlin
    dependencies {
        implementation("io.github.ezekielwachira:ccp:<LATEST_VERSION>")
    }
    ```

=== "Android (build.gradle)"

    ```groovy
    dependencies {
        implementation 'io.github.ezekielwachira:ccp:<LATEST_VERSION>'
    }
    ```

Gradle resolves the right artifact for each target — `ccp-android`, `ccp-iosarm64` or
`ccp-iossimulatorarm64`.

### iOS

There is nothing to add on the Xcode side. libPhoneNumber-iOS is compiled into the library's iOS
klib under a `CCP` symbol prefix, so no CocoaPod, Swift package or linker flag is needed, and an app
that also links libPhoneNumber-iOS itself does not hit duplicate symbols.

Show the components from your iOS source set like any other Compose Multiplatform UI:

```kotlin
fun MainViewController(): UIViewController = ComposeUIViewController {
    var country by remember { mutableStateOf<Country?>(null) }
    CountrySelector(selectedCountry = country, onCountrySelected = { country = it })
}
```

The repository's `iosApp/` is a working Xcode project around the shared sample.

### JitPack (Android only)

JitPack also builds the library, but on Linux, which cannot produce the iOS artifacts — use it only
from Android-only projects:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}

// build.gradle.kts
dependencies {
    implementation("com.github.EzekielWachira:Country-Code-Picker:<LATEST_VERSION>")
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
