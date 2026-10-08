// The Android shell around the shared demo in :sample. AGP 9 compiles Kotlin itself ("built-in
// Kotlin"), so no kotlin-android plugin is applied.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    // Distinct from the library's com.ezzy.ccp: AGP 9 rejects two modules sharing a namespace.
    namespace = "com.ezzy.ccp.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ezzy.ccp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            // libphonenumber ships three metadata blobs; the picker only ever reaches
            // PhoneNumberUtil and AsYouTypeFormatter, which need the first:
            //
            //   PhoneNumberMetadataProto          ~235 KB  required
            //   ShortNumberMetadataProto           ~75 KB  only ShortNumberInfo reads it
            //   PhoneNumberAlternateFormatsProto   ~23 KB  only PhoneNumberMatcher reads it
            //
            // Dropping the two unused ones saves ~99 KB with no behaviour change. The sample keeps
            // them excluded so CI actually exercises the configuration the README recommends —
            // advice that is never built is advice that eventually stops working.
            //
            // :ccp's verifyPhoneNumberMetadataFootprint task fails the build if the library ever
            // starts using an API that would need these back.
            excludes += setOf(
                "/com/google/i18n/phonenumbers/data/ShortNumberMetadataProto*",
                "/com/google/i18n/phonenumbers/data/PhoneNumberAlternateFormatsProto*",
            )
        }
    }
}

dependencies {
    implementation(project(":sample"))
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
