package com.ezzy.ccp.screenshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.data.DefaultCountryDataSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryRegion
import com.ezzy.ccp.countrypicker.persistence.InMemoryRecentCountryStore
import com.ezzy.ccp.countrypicker.state.CountryPickerConfig
import com.ezzy.ccp.countrypicker.state.CountryPickerState
import com.ezzy.ccp.countrypicker.state.rememberCountryPickerState
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryListStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyles
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.countrypicker.theme.QuickPicksStyle
import com.ezzy.ccp.countrypicker.ui.CountryPickerPanel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * The picker itself, rendered through [CountryPickerPanel] — the sheet's content without the modal
 * window, which a screenshot of the root cannot see — framed the way the sheet frames it.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class SheetScreenshotTest : ScreenshotTestBase() {

    private val kenya = requireNotNull(DefaultCountryDataSource.findByIso2("KE"))
    private val france = requireNotNull(DefaultCountryDataSource.findByIso2("FR"))
    private val japan = requireNotNull(DefaultCountryDataSource.findByIso2("JP"))

    /** A panel with recents, opened, then [setUp] applied once. */
    @Composable
    private fun Panel(
        config: CountryPickerConfig = CountryPickerConfig(),
        selected: Set<Country> = setOf(kenya),
        style: CountryPickerStyle = CountryPickerTheme.style,
        setUp: CountryPickerState.() -> Unit = {},
    ) {
        val recents = InMemoryRecentCountryStore(listOf("KE", "GB", "US", "NG"))
        val state = rememberCountryPickerState(config = config, selectedCountries = selected, recentCountryStore = recents)
        LaunchedEffect(state) {
            state.open()
            state.setUp()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PANEL_HEIGHT)
                .clip(style.shapes.sheet)
                .background(style.colors.background)
                .padding(top = 18.dp),
        ) {
            CountryPickerPanel(
                state = state,
                onCountrySelected = {},
                onClose = {},
                recentCountryStore = recents,
                style = style,
            )
        }
    }

    @Test
    fun `sheet with a selection and recents`() = snapshot("sheet_default") { Panel() }

    @Test
    fun `sheet in dark theme`() = snapshot("sheet_dark", dark = true) { Panel() }

    @Test
    fun `sheet searching`() = snapshot("sheet_search") {
        Panel { updateSearchQuery("ger") }
    }

    @Test
    fun `a misspelled search offers suggestions`() = snapshot("sheet_search_suggestions") {
        Panel { updateSearchQuery("Germny") }
    }

    @Test
    fun `sheet filtered to a region`() = snapshot("sheet_region") {
        Panel { selectRegion(CountryRegion.Africa) }
    }

    @Test
    fun `multiple selection with the confirm bar`() = snapshot("sheet_multi") {
        Panel(
            config = CountryPickerDefaults.multiSelectConfig(maximumSelectionCount = 5),
            selected = setOf(france, japan),
        )
    }

    @Test
    fun `sheet right to left`() = snapshot("sheet_rtl", rtl = true) { Panel() }

    @Test
    fun `cupertino sheet`() = snapshot("sheet_cupertino") { Panel(style = CountryPickerStyles.cupertino()) }

    @Test
    fun `material sheet`() = snapshot("sheet_material") { Panel(style = CountryPickerStyles.material()) }

    @Test
    fun `minimal sheet`() = snapshot("sheet_minimal") { Panel(style = CountryPickerStyles.minimal()) }

    @Test
    fun `card rows with circle flags and recents as sections`() = snapshot("sheet_cards") {
        val base = CountryPickerStyles.signature()
        Panel(
            style = base.copy(
                layout = base.layout.copy(
                    listStyle = CountryListStyle.Cards,
                    flagStyle = CountryFlagStyle.Circle,
                    quickPicks = QuickPicksStyle.Sections,
                    showIsoCode = true,
                ),
            ),
        )
    }

    private companion object {
        val PANEL_HEIGHT = 760.dp
    }
}
