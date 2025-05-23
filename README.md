# 📱 Country Code Picker  

[![](https://jitpack.io/v/EzekielWachira/Country-Code-Picker.svg)](https://jitpack.io/#EzekielWachira/Country-Code-Picker)

A lightweight and easy-to-use library for selecting country codes, validating phone numbers, and formatting them based on country-specific rules.  

## ✨ Features  
- ✅ **Phone Number Validation** – Ensures phone numbers are valid based on the selected country code.  
- ✅ **Phone Number Formatting** – Automatically formats phone numbers according to country-specific formats.  

## 📦 Installation  

### Step 1: Add JitPack Repository  
Add the following to your root `settings.gradle` at the end of repositories:  

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
Add the dependency in your `build.gradle` (Module-level):

```gradle
dependencies {
    implementation 'com.github.EzekielWachira:Country-Code-Picker:v0.1.5'
}
```

### Step 3: Usage

```kt
PhoneNumberInput(
    onPhoneValueChange = { formatedPhone, unFormatedPhone, valid ->
        // formattedPhone: A string representing the phone number in a user-friendly format 
        //                 (e.g., "+1 415-555-0132" for US numbers).

        // unformattedPhone: A string containing the raw phone number without any formatting
        //                   (e.g., "+14155550132" for the same US number).

        // valid: A boolean indicating whether the phone number is valid based on 
        //        the input field's validation rules (true if valid, false otherwise).

        // Example usage:
        if (valid) {
            println("Formatted: $formattedPhone, Raw: $unformattedPhone")
        } else {
            println("Invalid phone number")
        }
    },
    countriesToShow = listOf(
        "US", "GB", "KE", "FR", "AU", "BR", "CA", "CN"
    ), // List of country codes for selectable countries in the phone input UI.

    autoDetectCountry = true, // Automatic country detection based on SIM/network.

    setCountry = "KE", // Sets Kenya ("KE") as the default selected country.
    showHeader = true
)
```
 ### Demo


https://github.com/user-attachments/assets/f2c7c5b9-85dc-4e4a-9909-4ae57490d561


### License

````MIT License

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
````
