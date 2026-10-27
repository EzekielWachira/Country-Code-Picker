pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CCP"

// The library (Android + iOS).
include(":ccp")

// The demo, shared between platforms: `:sample` holds the Compose UI in commonMain and produces the
// iOS framework that iosApp/ embeds; `:app` is the thin Android shell around the same UI.
include(":sample")
include(":app")

// Generates the baseline profile :ccp ships, by driving :app through the picker on a device.
include(":baselineprofile")
