package com.ezzy.ccp.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.theme.CountryFlagSource
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyles
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.theme.FlagImageShape
import com.ezzy.ccp.countrypicker.ui.CountryFlag
import com.ezzy.ccp.countrypicker.ui.CountrySelector
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * flagcdn artwork in its three shapes and every frame, from stand-in images at each flag's real
 * proportions ([FakeFlagImages.fake]): Switzerland is square, Nepal taller than wide, Qatar 11:28.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class FlagScreenshotTest : ScreenshotTestBase() {

    private val countries = listOf("KE", "CH", "NP", "QA", "JP").map { requireNotNull(DefaultCountryDataSource.findByIso2(it)) }

    @Composable
    private fun FlagRow(label: String, style: CountryFlagStyle, shape: FlagImageShape) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(label, modifier = Modifier.width(92.dp))
            countries.forEach { country ->
                CountryFlag(country = country, size = 32.dp, style = style, source = CountryFlagSource.FlagCdn(shape = shape))
            }
        }
    }

    @Test
    fun `the three shapes, bare`() = snapshot("flags_shapes", flagImages = FakeFlagImages::fake) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            FlagImageShape.entries.forEach { shape -> FlagRow(shape.name, CountryFlagStyle.Plain, shape) }
        }
    }

    @Test
    fun `the three shapes in every frame`() = snapshot("flags_frames", flagImages = FakeFlagImages::fake) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(CountryFlagStyle.Tile, CountryFlagStyle.Circle, CountryFlagStyle.Rounded).forEach { style ->
                FlagImageShape.entries.forEach { shape -> FlagRow("${style.name} ${shape.name.take(10)}", style, shape) }
            }
        }
    }

    @Test
    fun `flags fall back to emoji while offline`() = snapshot("flags_offline") {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            listOf(CountryFlagStyle.Tile, CountryFlagStyle.Circle, CountryFlagStyle.Plain).forEach { style ->
                FlagRow(style.name, style, FlagImageShape.Waving)
            }
        }
    }

    @Test
    fun `a selector with same-height flags`() = snapshot("flags_selector", flagImages = FakeFlagImages::fake) {
        val base = CountryPickerStyles.signature()
        CountryPickerTheme(
            base.copy(layout = base.layout.copy(flagSource = CountryFlagSource.FlagCdn(shape = FlagImageShape.OriginalSameHeight))),
        ) {
            CountrySelector(selectedCountry = countries.first(), onCountrySelected = {})
        }
    }
}
