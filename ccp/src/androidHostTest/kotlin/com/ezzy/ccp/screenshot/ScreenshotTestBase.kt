package com.ezzy.ccp.screenshot

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.ezzy.ccp.countrypicker.ui.LocalFlagImageLoader
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule

/**
 * Shared scaffolding for the screenshot suite.
 *
 * Every case renders through [snapshot] so the frame — theme, background, padding and width — is
 * identical across the whole suite. A diff then always means the component changed, never that one
 * test happened to wrap it differently.
 *
 * Goldens live in `ccp/src/androidHostTest/screenshots` and are committed. Regenerate deliberately with
 * `./gradlew :ccp:recordRoborazziDebug` and review the image diff; `verifyRoborazziDebug` in CI
 * fails when a render drifts from what was committed.
 */
abstract class ScreenshotTestBase {

    @get:Rule
    val compose = createComposeRule()

    /**
     * Renders [content] and writes it to `<name>.png`.
     *
     * @param dark Renders against the dark color scheme. Worth its own golden for anything with a
     *   container, border or tonal background — the places a hardcoded color hides until someone
     *   switches theme.
     * @param rtl Renders under a right-to-left layout direction. The bug this catches (dial codes
     *   and numbers reordering) reproduces with English text, so it does not need Arabic strings.
     * @param flagImages Serves flag images: offline by default, so flags show their emoji fallback
     *   and no golden depends on the network. See [FakeFlagImages].
     */
    protected fun snapshot(
        name: String,
        dark: Boolean = false,
        rtl: Boolean = false,
        flagImages: (Context) -> ImageLoader = FakeFlagImages::offline,
        content: @Composable () -> Unit,
    ) {
        compose.setContent {
            val context = LocalContext.current
            val loader = remember { flagImages(context) }
            MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
                    LocalFlagImageLoader provides loader,
                ) {
                    Surface {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            content = { content() },
                        )
                    }
                }
            }
        }
        compose.onRoot().captureRoboImage("src/androidHostTest/screenshots/$name.png", roborazziOptions = OPTIONS)
    }

    private companion object {
        /**
         * Up to 1% of pixels may differ. Goldens are recorded on macOS and verified on CI's Linux
         * runner, whose text antialiasing differs: measured at 0.2–0.65% of pixels, all along glyph
         * edges. A real regression — a moved element, a changed color, a missing icon — changes far
         * more than that.
         */
        val OPTIONS = RoborazziOptions(
            compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01f),
        )
    }
}
