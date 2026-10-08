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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountrySelectionMode
import com.ezzy.ccp.countrypicker.state.CountryPickerState
import com.ezzy.ccp.countrypicker.state.CountrySection
import com.ezzy.ccp.countrypicker.state.CountrySectionKind
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryListStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_section_selected_count
import org.jetbrains.compose.resources.stringResource

/**
 * The country list, laid out in the style's [CountryListStyle].
 *
 * ### Item structure
 * Each titled section contributes exactly one header item followed by one item per country, and
 * nothing else — the A–Z rail ([rememberCountryListIndex]) maps letters to lazy-list indices by that
 * rule. Content added through [leadingContent] comes before every section; a caller that adds some
 * offsets the rail's indices by the number of items it added.
 *
 * ### Motion
 * Rows fade in with a short stagger the first time the list appears, and slide rather than jump when
 * a country moves between sections. Both are off when motion is.
 *
 * @param leadingContent Items placed before the first section, such as a carousel or a banner.
 */
@Composable
public fun CountryList(
    sections: List<CountrySection>,
    selectedCountries: Set<Country>,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: CountrySelectionMode = CountrySelectionMode.Single,
    disabledCountries: Set<String> = emptySet(),
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(bottom = LIST_BOTTOM_PADDING),
    leadingContent: LazyListScope.() -> Unit = {},
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
) {
    val motion = style.motion
    // One timeline for the whole entrance; each row derives its own progress from it in the draw
    // phase, so the stagger costs no recomposition.
    val entrance = remember { Animatable(if (motion.enabled) 0f else 1f) }
    val entranceMillis = motion.staggerMillis * motion.maxStaggeredItems + ENTRANCE_ITEM_MILLIS
    LaunchedEffect(Unit) {
        if (entrance.value < 1f) entrance.animateTo(1f, tween(entranceMillis, easing = LinearEasing))
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
    ) {
        leadingContent()

        var rowIndex = 0
        sections.forEachIndexed { sectionIndex, section ->
            if (section.kind.titleRes != null) {
                val header: @Composable LazyItemScope.() -> Unit = {
                    CountrySectionHeader(
                        section = section,
                        showCount = selectionMode == CountrySelectionMode.Multiple && section.kind == CountrySectionKind.Selected,
                        isFirst = sectionIndex == 0,
                        style = style,
                    )
                }
                // Grouped and card lists keep their headers in flow: a canvas-colored bar floating
                // over white cards looks broken. Plain lists pin them, so a long tail never loses
                // track of which group a row belongs to.
                if (style.layout.listStyle == CountryListStyle.Plain) {
                    stickyHeader(key = "header_${section.kind.name}", contentType = HEADER_CONTENT_TYPE) { header() }
                } else {
                    item(key = "header_${section.kind.name}", contentType = HEADER_CONTENT_TYPE) { header() }
                }
            }

            val firstRow = rowIndex
            items(
                count = section.items.size,
                key = { "${section.kind.name}_${section.items[it].country.iso2Code}" },
                contentType = { ROW_CONTENT_TYPE },
            ) { index ->
                val match = section.items[index]
                val country = match.country
                val selected = country in selectedCountries
                val enabled = country.iso2Code !in disabledCountries
                val position = GroupPosition.of(index, section.items.size)
                val order = firstRow + index
                val itemModifier = Modifier
                    .animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = motion.offset)
                    .graphicsLayer {
                        val stagger = (order.coerceAtMost(motion.maxStaggeredItems) * motion.staggerMillis).toFloat()
                        val local = ((entrance.value * entranceMillis - stagger) / ENTRANCE_ITEM_MILLIS).coerceIn(0f, 1f)
                        alpha = local
                        translationY = (1f - local) * ENTRANCE_OFFSET.toPx()
                    }
                    .listItemContainer(style, position, untitledFirst = section.kind.titleRes == null && index == 0 && sectionIndex == 0)

                val onClick = { onCountryClick(country) }
                Box(itemModifier) {
                    if (listItemContent != null) {
                        listItemContent(CountryListItemScope(match, selected, enabled, selectionMode, onClick))
                    } else {
                        CountryListItem(
                            match = match,
                            selected = selected,
                            onClick = onClick,
                            enabled = enabled,
                            selectionMode = selectionMode,
                            unavailable = !enabled,
                            shape = style.rowShape(),
                            style = style,
                            flagContent = flagContent,
                        )
                    }
                }
            }
            rowIndex += section.items.size
        }
    }
}

/** [CountryList] driven by a [CountryPickerState]: its sections, selection and disabled countries. */
@Composable
public fun CountryList(
    state: CountryPickerState,
    onCountryClick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(bottom = LIST_BOTTOM_PADDING),
    leadingContent: LazyListScope.() -> Unit = {},
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
    listItemContent: (@Composable (CountryListItemScope) -> Unit)? = null,
) {
    val sections by state.sections
    val pending by state.pendingSelection
    CountryList(
        sections = sections,
        // Multiple selection shows the pending set so a tick is visible immediately; single
        // selection falls back to the confirmed value until the user taps something.
        selectedCountries = when {
            state.config.isMultiSelect -> pending
            pending.isNotEmpty() -> pending
            else -> state.confirmedSelection
        },
        onCountryClick = onCountryClick,
        modifier = modifier,
        selectionMode = state.config.selectionMode,
        disabledCountries = state.config.disabledCountryCodes,
        listState = listState,
        contentPadding = contentPadding,
        leadingContent = leadingContent,
        style = style,
        flagContent = flagContent,
        listItemContent = listItemContent,
    )
}

/**
 * A section's title, in small capitals above its group.
 *
 * @param showCount Appends the count — "SELECTED · 3" — used for the selected group in multiple
 *   selection.
 * @param isFirst Drops the top spacing for the first section, which sits directly under the header.
 */
@Composable
public fun CountrySectionHeader(
    section: CountrySection,
    modifier: Modifier = Modifier,
    showCount: Boolean = false,
    isFirst: Boolean = false,
    style: CountryPickerStyle = CountryPickerTheme.style,
) {
    val titleRes = section.kind.titleRes ?: return
    val title = if (showCount) {
        stringResource(Res.string.ccp_section_selected_count, section.count)
    } else {
        stringResource(titleRes)
    }
    val plain = style.layout.listStyle == CountryListStyle.Plain
    CapsLabel(
        text = title,
        style = style.typography.sectionLabel,
        color = if (plain) style.colors.accent else style.colors.textSecondary,
        modifier = modifier
            .fillMaxWidth()
            .background(if (plain) style.colors.background else androidx.compose.ui.graphics.Color.Transparent)
            .padding(
                start = style.sectionInset() + 4.dp,
                end = style.sectionInset(),
                top = if (isFirst) 6.dp else style.dimensions.groupSpacing,
                bottom = 8.dp,
            )
            .semantics { heading() },
    )
}

/** Where a row sits in its group, which decides which of the group's corners it draws. */
internal enum class GroupPosition {
    Single, First, Middle, Last;

    companion object {
        fun of(index: Int, count: Int): GroupPosition = when {
            count == 1 -> Single
            index == 0 -> First
            index == count - 1 -> Last
            else -> Middle
        }
    }
}

/** The clip shape each row uses for its highlight and press feedback. */
private fun CountryPickerStyle.rowShape(): Shape = when (layout.listStyle) {
    // The group clips the corners; the row's own highlight fills its band edge to edge.
    CountryListStyle.InsetGrouped -> RectangleShape
    CountryListStyle.Plain, CountryListStyle.Cards -> shapes.row
}

/** Horizontal inset of a section from the edge of the sheet. */
private fun CountryPickerStyle.sectionInset(): Dp = when (layout.listStyle) {
    CountryListStyle.Plain -> dimensions.sheetHorizontalPadding
    CountryListStyle.InsetGrouped, CountryListStyle.Cards -> dimensions.groupHorizontalMargin
}

/**
 * Draws the container a row sits in: its slice of a grouped card (with only the corners, edges and
 * divider that belong to its [position]), its own card, or — for plain lists — just an inset divider.
 */
private fun Modifier.listItemContainer(
    style: CountryPickerStyle,
    position: GroupPosition,
    untitledFirst: Boolean,
): Modifier {
    val colors = style.colors
    val dimensions = style.dimensions
    val showDivider = style.layout.showDividers && (position == GroupPosition.First || position == GroupPosition.Middle)
    // Where the divider starts: after the flag, so it reads as separating names rather than rows.
    val flagWidth = if (style.layout.flagStyle == CountryFlagStyle.Hidden) 0.dp else dimensions.flagSizeRow
    val dividerInset = dimensions.rowHorizontalPadding + flagWidth + dimensions.rowContentSpacing

    return when (style.layout.listStyle) {
        CountryListStyle.InsetGrouped -> {
            val r = style.shapes.groupCornerRadius
            val shape = when (position) {
                GroupPosition.Single -> RoundedCornerShape(r)
                GroupPosition.First -> RoundedCornerShape(topStart = r, topEnd = r)
                GroupPosition.Last -> RoundedCornerShape(bottomStart = r, bottomEnd = r)
                GroupPosition.Middle -> RectangleShape
            }
            this
                .padding(horizontal = dimensions.groupHorizontalMargin)
                .padding(top = if (untitledFirst) 6.dp else 0.dp)
                .drawWithContent {
                    drawContent()
                    // This row's slice of the group outline: a rounded rectangle that runs past the
                    // row on any side it shares with a neighbour, clipped to the row. The shared
                    // edges fall outside the clip, and the side edges continue unbroken into the
                    // next row's slice.
                    val stroke = HAIRLINE.toPx()
                    val half = stroke / 2
                    val overrun = r.toPx() + stroke * 4
                    val opensUp = position == GroupPosition.Middle || position == GroupPosition.Last
                    val opensDown = position == GroupPosition.First || position == GroupPosition.Middle
                    val outline = Path().apply {
                        addRoundRect(
                            RoundRect(
                                left = half,
                                top = if (opensUp) -overrun else half,
                                right = size.width - half,
                                bottom = if (opensDown) size.height + overrun else size.height - half,
                                cornerRadius = CornerRadius((r.toPx() - half).coerceAtLeast(0f)),
                            ),
                        )
                    }
                    clipRect { drawPath(outline, color = colors.hairline, style = Stroke(stroke)) }
                    if (showDivider) drawDivider(dividerInset.toPx(), dimensions.rowHorizontalPadding.toPx(), colors.hairline, stroke)
                }
                .background(colors.surface, shape)
                .clip(shape)
        }
        CountryListStyle.Cards -> this
            .padding(horizontal = dimensions.groupHorizontalMargin, vertical = 4.dp)
            .pickerShadow(style.elevation.tile, style.shapes.row, colors.shadow)
            .background(colors.surface, style.shapes.row)
            .border(HAIRLINE, colors.hairline, style.shapes.row)
        CountryListStyle.Plain -> this
            .padding(horizontal = PLAIN_ROW_INSET)
            .drawWithContent {
                drawContent()
                if (showDivider) drawDivider(dividerInset.toPx(), dimensions.rowHorizontalPadding.toPx(), colors.hairline, HAIRLINE.toPx())
            }
    }
}

/** A hairline across the bottom of the row, inset from the start (and mirrored in RTL). */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDivider(
    startInset: Float,
    endInset: Float,
    color: androidx.compose.ui.graphics.Color,
    stroke: Float,
) {
    val y = size.height - stroke / 2
    val (x0, x1) = if (layoutDirection == LayoutDirection.Ltr) {
        startInset to size.width - endInset
    } else {
        endInset to size.width - startInset
    }
    drawLine(color = color, start = Offset(x0, y), end = Offset(x1, y), strokeWidth = stroke)
}

private const val HEADER_CONTENT_TYPE = "header"
private const val ROW_CONTENT_TYPE = "row"
private const val ENTRANCE_ITEM_MILLIS = 260
private val ENTRANCE_OFFSET = 10.dp
private val HAIRLINE = 0.75.dp

/** How far a plain list's rows — and their selection highlight — sit from the edges. */
internal val PLAIN_ROW_INSET = 8.dp
private val LIST_BOTTOM_PADDING = 28.dp
