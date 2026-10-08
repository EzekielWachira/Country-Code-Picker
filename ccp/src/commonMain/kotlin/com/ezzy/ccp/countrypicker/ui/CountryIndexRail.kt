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

package com.ezzy.ccp.countrypicker.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryTextNormalizer
import com.ezzy.ccp.countrypicker.state.CountrySection
import com.ezzy.ccp.countrypicker.state.CountrySectionKind
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography

/**
 * A lookup from a country (or an initial letter) to its row index in the flat `LazyColumn` that
 * [CountryList] emits.
 *
 * The translation is the whole point. `CountryList` renders a list of [CountrySection]s, each
 * optionally preceded by a sticky header, into one flat lazy list — so the *n*th country is not at
 * lazy index *n*, and anything that wants to scroll to a specific country has to account for every
 * header and every preceding section. Doing that arithmetic at each call site is how off-by-one
 * scroll bugs get written; doing it once, here, from the same `sections` the list was built from,
 * is how they do not.
 *
 * Build one with [rememberCountryListIndex].
 *
 * @property letters The initial letters actually present in the list, in display order. Only
 *   letters with at least one country appear — a rail that offers `Q` and then does nothing when
 *   tapped is worse than a rail that omits it.
 */
@Immutable
public class CountryListIndex internal constructor(
    public val letters: List<Char>,
    private val lazyIndexByIso2: Map<String, Int>,
    private val lazyIndexByLetter: Map<Char, Int>,
) {

    /** The lazy-list index of [iso2Code]'s row, or `null` when that country is not in the list. */
    public fun lazyIndexOf(iso2Code: String): Int? = lazyIndexByIso2[iso2Code.uppercase()]

    /** The lazy-list index of the first country starting with [letter], or `null` if there is none. */
    public fun lazyIndexOf(letter: Char): Int? = lazyIndexByLetter[letter.uppercaseChar()]

    /** Convenience for [lazyIndexOf] by country. */
    public fun lazyIndexOf(country: Country): Int? = lazyIndexOf(country.iso2Code)

    internal companion object {
        val Empty = CountryListIndex(emptyList(), emptyMap(), emptyMap())
    }
}

/**
 * Builds the [CountryListIndex] for [sections], recomputed only when the sections change.
 *
 * Letters are taken from the *unsectioned* group only ([CountrySectionKind.All]). Indexing the
 * "Recently selected" or "Suggested" groups too would make one letter map to several places and the
 * rail jump backwards — those groups are short, deliberately at the top, and reachable by scrolling.
 *
 * The initial letter is read from the accent-folded name, so `Åland` indexes under `A` rather than
 * after `Z` where a raw comparison puts it.
 */
@Composable
public fun rememberCountryListIndex(sections: List<CountrySection>): CountryListIndex =
    remember(sections) { buildCountryListIndex(sections) }

internal fun buildCountryListIndex(sections: List<CountrySection>): CountryListIndex {
    if (sections.isEmpty()) return CountryListIndex.Empty

    val byIso2 = HashMap<String, Int>()
    val byLetter = LinkedHashMap<Char, Int>()
    var lazyIndex = 0

    sections.forEach { section ->
        // Mirrors CountryList exactly: a titled section emits one header item before its rows.
        if (section.kind.titleRes != null) lazyIndex++

        section.items.forEach { match ->
            val country = match.country
            byIso2[country.iso2Code] = lazyIndex

            if (section.kind == CountrySectionKind.All) {
                val initial = CountryTextNormalizer.normalize(country.displayName)
                    .firstOrNull { it.isLetter() }
                    ?.uppercaseChar()
                // putIfAbsent semantics: the first row for a letter is the one to scroll to.
                if (initial != null && initial !in byLetter) byLetter[initial] = lazyIndex
            }
            lazyIndex++
        }
    }

    return CountryListIndex(
        letters = byLetter.keys.toList(),
        lazyIndexByIso2 = byIso2,
        lazyIndexByLetter = byLetter,
    )
}

/**
 * The A–Z rail down the edge of a long country list.
 *
 * 236 rows is roughly fifteen screens. Search covers the case where the user knows the name; this
 * covers the case where they are browsing, or know only roughly where a country falls, and it is
 * the interaction people already expect from a long indexed list on both platforms.
 *
 * ### Interaction
 * Tap a letter to jump; drag along the rail to scrub continuously, which is the faster gesture and
 * the reason this is not simply a column of buttons. A light haptic fires on each letter *change*
 * during a drag — not on each pointer event — so scrubbing feels like passing detents rather than
 * one continuous buzz.
 *
 * ### Accessibility
 * The rail is hidden from the accessibility tree (`clearAndSetSemantics {}`). It is a redundant
 * shortcut to rows a screen-reader user already reaches by swiping through the list, and exposing
 * 26 unlabelled single-character targets in front of that list makes the list harder to use, not
 * easier. Search remains the accessible fast path.
 *
 * @param letters Letters to draw — pass [CountryListIndex.letters] so the rail only offers letters
 *   that lead somewhere.
 * @param onLetterSelected Called with a letter when it is tapped or scrubbed onto. Called once per
 *   letter change, not once per pointer event.
 */
@Composable
public fun CountryIndexRail(
    letters: List<Char>,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
) {
    if (letters.isEmpty()) return

    val haptic = LocalHapticFeedback.current
    var railHeightPx by remember { mutableIntStateOf(0) }
    var activeLetter by remember { mutableStateOf<Char?>(null) }

    // Maps a y offset within the rail to a letter. The rail draws its letters evenly spaced across
    // its own height, so position maps to index by simple proportion — deriving it from the touch
    // position rather than from per-letter hit boxes is what makes a drag continuous instead of
    // only registering when the finger lands inside a glyph.
    fun letterAt(y: Float): Char? {
        if (railHeightPx <= 0) return null
        val fraction = (y / railHeightPx).coerceIn(0f, 0.999f)
        return letters.getOrNull((fraction * letters.size).toInt())
    }

    fun select(letter: Char?) {
        if (letter == null || letter == activeLetter) return
        activeLetter = letter
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onLetterSelected(letter)
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(RAIL_WIDTH)
            .padding(vertical = RAIL_VERTICAL_PADDING)
            .onSizeChanged { railHeightPx = it.height }
            .pointerInput(letters) {
                detectVerticalDragGestures(
                    onDragStart = { select(letterAt(it.y)) },
                    // Clearing the active letter on release lets the user scrub back onto the same
                    // letter later and have it register again.
                    onDragEnd = { activeLetter = null },
                    onDragCancel = { activeLetter = null },
                    onVerticalDrag = { change, _ -> select(letterAt(change.position.y)) },
                )
            }
            .pointerInput(letters) {
                // A separate detector: detectVerticalDragGestures never fires for a tap that does
                // not move, which is most taps on a rail this narrow.
                detectTapGestures { offset ->
                    activeLetter = null
                    select(letterAt(offset.y))
                }
            }
            .clearAndSetSemantics {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        letters.forEach { letter ->
            Text(
                text = letter.toString(),
                style = typography.countryMetadata.copy(
                    fontWeight = if (letter == activeLetter) FontWeight.Bold else FontWeight.Medium,
                ),
                color = if (letter == activeLetter) {
                    colors.selectorFocusedBorder
                } else {
                    colors.rowSecondaryContent
                },
            )
        }
    }
}

/** Narrow enough not to steal horizontal space from names, wide enough to hit. */
private val RAIL_WIDTH: Dp = 24.dp

/** Keeps the first and last letters clear of the list's own rounded corners. */
private val RAIL_VERTICAL_PADDING: Dp = 8.dp
