# 📱 Country Code Picker

[![](https://jitpack.io/v/EzekielWachira/Country-Code-Picker.svg)](https://jitpack.io/#EzekielWachira/Country-Code-Picker)

A lightweight and customizable Jetpack Compose library for country code selection, phone number validation, and automatic formatting based on country-specific rules.

## ✨ Features

✅ **Country Picker** – Choose from a searchable list of countries with flags and dial codes.

✅ **Phone Number Validation** – Ensures phone numbers are valid per country rules (powered by Google libphonenumber).

✅ **Phone Number Formatting** – Automatically formats numbers into E.164, international, and national formats.

✅ **Auto Country Detection** – Detects country from SIM, network, or device locale.

✅ **Highly Customizable** – Colors, shapes, styles, and visibility options are fully configurable.

✅ **Compose-First API** – Built natively for Jetpack Compose.

## 📦 Installation

### Step 1: Add JitPack Repository

In your root `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the Dependency

```gradle
dependencies {
    implementation 'com.github.EzekielWachira:Country-Code-Picker:v0.1.6'
}
```

## 🚀 Usage

```kotlin
PhoneNumberInput(
    value = setPhone.toString(),
    setCountry = "KE",
    onDone = {
        Toast.makeText(this@MainActivity, "On Done", Toast.LENGTH_SHORT)
            .show()
    },
    onValueChange = { (formattedPhone, phoneNumber, country, isValid) ->
        // formattedPhone -> formatted phone number
        // phoneNumber -> unformatted phone number
        // country -> selected country
        // isValid -> true if valid, false otherwise
    },
    ccpConfig = CCPDefaults.defaultConfig(
        showCountriesHeaderDivider = true,
        autoDetectCountry = true,
        phoneInputShape = RoundedCornerShape(8.dp)
    ),
    countriesToShow = listOf("US", "GB", "KE", "FR", "AU", "BR", "CA", "CN"),
    // Limits the selectable countries in the bottom sheet.

    setCountry = "KE",
    // Preselects Kenya ("KE") as default. Overrides auto-detection if provided.

    autoDetectCountry = true,
    // Automatically detects country from SIM → network → locale (fallback: "US").

    showHeader = true
    // Displays header & search bar in country selection sheet.
)
```

### Example Callback Data

If the user inputs `0724154958` with Kenya selected:

- **Formatted**: +254 724 154 958
- **Raw (E.164)**: +254724154958
- **Valid**: true

## 🎨 Customization

You can fully customize colors, shapes, and styles using:

- **CCPColors** → controls input, border, cursor, and sheet colors.
- **CCPConfig** → controls shapes, typography, visibility of flags/dial codes, etc.

### Example:

```kotlin
PhoneNumberInput(
    colors = CCPColors(
        containerColor = Color.White,
        borderColor = Color.Gray,
        cursorColor = Color.Black,
        inputTextColor = Color.DarkGray,
        phoneHintColor = Color.LightGray,
        countryCodeTextColor = Color.Black,
        countryChevronColor = Color.Gray,
        ccpSheetColor = CCPSheetColor.Default
    ),
    ccpConfig = CCPConfig(
        autoDetectCountry = true,
        showHeader = true,
        showCountryFlag = true,
        showDialCodeCountryItem = true,
        showFlagCountryItem = true
    )
)
```

## 📖 API Reference

### Phone

Represents a parsed phone number with validation info.

```kotlin
data class Phone(
    val formattedPhone: String,  // International format (e.g., +1 415-555-0132)
    val phoneNumber: String,     // Raw E.164 format (e.g., +14155550132)
    val country: SelectedCountry?, // Country info (dial code, ISO, flag, etc.)
    val isValid: Boolean          // Whether the number is valid
)
```

### PhoneValidationResult

Returned after validating or parsing a phone number.

```kotlin
data class PhoneValidationResult(
    val formattedNumber: String,              // Pretty format (e.g., +254 712 345 678)
    val unformattedNumber: String,            // E.164 format (e.g., +254712345678)
    val formattedWithoutCountryCode: String,  // National format (e.g., 0712 345 678)
    val isValid: Boolean                      // True if valid, false otherwise
)
```

### CCPColors

Defines all colors used in the phone input and country picker UI.

```kotlin
data class CCPColors(
    val containerColor: Color,         // Background color of the input field
    val cursorColor: Color,            // Cursor color inside the text field
    val borderColor: Color,            // Border color of the input container
    val inputTextColor: Color,         // Phone number text color
    val phoneHintColor: Color,         // Hint/placeholder text color
    val countryCodeTextColor: Color,   // Country dial code text color
    val countryChevronColor: Color,    // Chevron (dropdown arrow) color
    val ccpSheetColor: CCPSheetColor   // Bottom sheet background + accents
)
```

### CCPConfig

Defines styles, shapes, and behavior of the component.

```kotlin
data class CCPConfig(
    val phoneInputCornerRadius: Dp,  
    val phoneHintStyle: TextStyle,  
    val borderWidth: Dp,  
    val autoDetectCountry: Boolean,  // Detect SIM/network/locale
    val showHeader: Boolean,         // Show header in country picker sheet
    val showCountryFlag: Boolean,    // Show flag in input field
    val countriesSheetShape: Shape,  
    val searchBorderWidth: Dp,  
    val searchCornerRadius: Dp,  
    val searchHintStyle: TextStyle,  
    val headerStyle: TextStyle,  
    val showDialCodeCountryItem: Boolean,  
    val showFlagCountryItem: Boolean,  
    val countryItemDialCodeTextStyle: TextStyle,  
    val countryItemNameTextStyle: TextStyle,  
    val countryItemShape: Shape,  
    val phoneInputShape: Shape,  
    val showCountriesHeaderDivider: Boolean  
)
```

## 📸 Demo

https://github.com/user-attachments/assets/f2c7c5b9-85dc-4e4a-9909-4ae57490d561

## 📜 License

```
Copyright (c) 2025 Ezekiel Wachira

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```