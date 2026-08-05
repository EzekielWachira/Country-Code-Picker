plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.ezzy.ccp"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        // Library modules have no app-facing targetSdk, but AGP still uses this value for the
        // generated androidTest APK. Left unset it silently falls back to minSdk (24), which is old
        // enough that some OS builds route the test app through the legacy permission-review flow
        // (a GrantPermissionsActivity dialog steals focus right as the test Activity launches, and
        // Compose UI tests fail with "No compose hierarchies found"). Matching compileSdk avoids it.
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            // ModalBottomSheet, clickable Surface and stickyHeader are still marked experimental but
            // are the correct Material 3 / Foundation APIs for this component. Opting in once here is
            // clearer than annotating every composable that touches them.
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        )
    }
    buildFeatures {
        compose = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.lib.phone)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}


publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.ezzy"
            artifactId = "ccp"
            version = "0.0.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}