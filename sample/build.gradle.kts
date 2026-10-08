import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// The demo UI, shared by both platforms: :app wraps it in an Activity, iosApp/ in a
// UIViewController. Not published.
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    android {
        namespace = "com.ezzy.ccp.sample"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        // The framework iosApp/ links. Static, so the app ships one binary and the vendored
        // phone-number engine inside :ccp's klib is linked straight into it.
        target.binaries.framework {
            baseName = "CCPSample"
            isStatic = true
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":ccp"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui.tooling.preview)
        }
    }
}
