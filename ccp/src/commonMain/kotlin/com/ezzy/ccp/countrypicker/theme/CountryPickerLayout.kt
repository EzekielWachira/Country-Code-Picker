/**
 * Copyright (c) 2025 Ezekiel Wachira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ezzy.ccp.countrypicker.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** How the picker is presented when it opens. */
public enum class PickerPresentation {
    /**
     * A bottom sheet on phones and a centered dialog on wide windows (tablets, foldables, desktop),
     * switching at [CountryPickerLayout.wideScreenBreakpoint].
     */
    Adaptive,

    /** Always a bottom sheet. */
    BottomSheet,

    /** Always a bottom sheet that opens to the full height of the screen. */
    FullScreenSheet,

    /** Always a centered dialog. */
    Dialog,
}

/** How the country list is laid out. */
public enum class CountryListStyle {
    /** Each section is a rounded card inset from the edges, rows separated by hairlines. */
    InsetGrouped,

    /** Edge-to-edge rows; the selected row is a rounded highlight. */
    Plain,

    /** Every row is its own rounded card. */
    Cards,
}

/** How a flag is drawn. */
public enum class CountryFlagStyle {
    /** The flag centered on a softly shaded rounded tile — the Signature look. */
    Tile,

    /** Cropped into a circle. */
    Circle,

    /** Flat, cropped to a 4:3 rounded rectangle — the same size for every flag. */
    Rounded,

    /** The bare emoji glyph at its natural proportions. */
    Plain,

    /** No flag. Fields fall back to text; rows drop the leading visual. */
    Hidden,
}

/** What marks the selected row in single selection. Multiple selection always uses a checkbox. */
public enum class SelectionIndicator {
    /** An animated check mark that draws itself in. */
    Check,

    /** A radio button. */
    Radio,

    /** No mark; the row's tint alone signals selection. Not recommended — see the accessibility guide. */
    None,
}

/** The sheet's title area. */
public enum class SheetHeaderStyle {
    /** A large title and subtitle. */
    Large,

    /** A single compact title row. */
    Compact,
}

/** How recently used, suggested and detected countries are surfaced. */
public enum class QuickPicksStyle {
    /** A horizontally scrolling row of tiles above the list. */
    Carousel,

    /** Their own titled sections at the top of the list. */
    Sections,

    /** Not surfaced. */
    Hidden,
}

/**
 * Structural choices — the parts of the design that are about arrangement rather than color or size.
 *
 * Behaviour (which countries, single or multiple selection, limits) lives in
 * [com.ezzy.ccp.countrypicker.state.CountryPickerConfig]; everything about how the picker *looks* is
 * here and in the rest of [CountryPickerStyle].
 *
 * @property presentation Sheet, dialog, or adaptive.
 * @property listStyle Grouped, plain or card rows.
 * @property flagStyle How flags are framed everywhere — tile, circle, rounded or bare.
 * @property flagSource Where flag artwork comes from: flagcdn.com images in one of three shapes, or
 *   the platform's emoji. See [CountryFlagSource].
 * @property selectionIndicator Check, radio or none, for single selection.
 * @property headerStyle Large or compact sheet title.
 * @property quickPicks How recent, suggested and detected countries are surfaced.
 * @property showRegionFilters The sliding region filter under the search field.
 * @property showRegionCounts The number of countries on each region filter.
 * @property showDialCode Dial codes trailing each row.
 * @property showIsoCode ISO codes in each row's secondary line.
 * @property showRegionName Region names in each row's secondary line.
 * @property showDividers Hairlines between rows.
 * @property showAlphabetIndex The scrubbable A–Z rail beside the full list.
 * @property showResultCount The result count while searching.
 * @property highlightSearchMatches Emphasis on the part of a name that matched the query.
 * @property showSearchSuggestions "Did you mean …" suggestions when a search finds nothing.
 * @property sheetHeightFraction Height of a non-full-screen sheet, as a fraction of the window.
 * @property dialogMaxWidth Width cap of the dialog presentation.
 * @property dialogMaxHeight Height cap of the dialog presentation.
 * @property wideScreenBreakpoint Window width at which [PickerPresentation.Adaptive] switches to a
 *   dialog.
 */
@Immutable
public data class CountryPickerLayout(
    val presentation: PickerPresentation = PickerPresentation.Adaptive,
    val listStyle: CountryListStyle = CountryListStyle.InsetGrouped,
    val flagStyle: CountryFlagStyle = CountryFlagStyle.Tile,
    val flagSource: CountryFlagSource = CountryFlagSource.FlagCdn(),
    val selectionIndicator: SelectionIndicator = SelectionIndicator.Check,
    val headerStyle: SheetHeaderStyle = SheetHeaderStyle.Large,
    val quickPicks: QuickPicksStyle = QuickPicksStyle.Carousel,
    val showRegionFilters: Boolean = true,
    val showRegionCounts: Boolean = true,
    val showDialCode: Boolean = true,
    val showIsoCode: Boolean = false,
    val showRegionName: Boolean = false,
    val showDividers: Boolean = true,
    val showAlphabetIndex: Boolean = true,
    val showResultCount: Boolean = true,
    val highlightSearchMatches: Boolean = true,
    val showSearchSuggestions: Boolean = true,
    val sheetHeightFraction: Float = 0.92f,
    val dialogMaxWidth: Dp = 560.dp,
    val dialogMaxHeight: Dp = 760.dp,
    val wideScreenBreakpoint: Dp = 600.dp,
)
