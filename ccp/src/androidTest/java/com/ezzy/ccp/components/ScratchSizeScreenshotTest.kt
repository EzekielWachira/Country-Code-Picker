package com.ezzy.ccp.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.ezzy.ccp.countrypicker.theme.PhoneFieldSize
import com.ezzy.ccp.utils.CCPDefaults
import org.junit.Rule
import org.junit.Test
import java.io.File

class ScratchSizeScreenshotTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun captureAllSizes() {
        rule.setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PhoneNumberInput(
                            setCountry = "KE",
                            label = "Regular",
                            ccpConfig = CCPDefaults.defaultConfig(showLabel = true, phoneFieldSize = PhoneFieldSize.Regular),
                        )
                        PhoneNumberInput(
                            setCountry = "KE",
                            label = "Compact",
                            ccpConfig = CCPDefaults.defaultConfig(showLabel = true, phoneFieldSize = PhoneFieldSize.Compact),
                        )
                        PhoneNumberInput(
                            setCountry = "KE",
                            label = "Extra Compact",
                            ccpConfig = CCPDefaults.defaultConfig(showLabel = true, phoneFieldSize = PhoneFieldSize.ExtraCompact),
                        )
                    }
                }
            }
        }
        rule.waitForIdle()
        val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
        val dir = InstrumentationRegistry.getInstrumentation().targetContext.filesDir
        val file = File(dir, "scratch_sizes.png")
        file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
    }
}
