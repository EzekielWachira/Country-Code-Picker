# 📱 Country Code Picker  

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
    implementation 'com.github.EzekielWachira:Country-Code-Picker:v0.0.1'
}
```

### Step 3: Usage

```kt
PhoneFieldComponent(
    onPhoneValueChange = { formatedPhone, unFormatedPhone, valid ->
        //Use the above values
    }   
)
```

