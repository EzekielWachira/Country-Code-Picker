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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.CountryPickerBidi
import com.ezzy.ccp.countrypicker.state.CountryQuickPick
import com.ezzy.ccp.countrypicker.state.QuickPickSource
import com.ezzy.ccp.countrypicker.theme.CountryFlagStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_a11y_row
import com.ezzy.ccp.resources.ccp_detected_badge
import com.ezzy.ccp.resources.ccp_section_quick_picks
import org.jetbrains.compose.resources.stringResource

/**
 * A horizontally scrolling row of one-tap shortcuts: the detected country, recent choices and the
 * host's suggestions, as tiles above the full list.
 *
 * Each tile shows the flag, ISO code and dial code — enough to recognise a country at a glance
 * without reading its name. The detected country carries a location marker, and selected countries a
 * check. Tiles behave like the list's rows for accessibility: one target each, announced with their
 * name, dial code and selection state.
 *
 * @param picks The shortcuts, in order — see
 *   [com.ezzy.ccp.countrypicker.state.CountryPickerState.quickPicks].
 * @param selectedCountries Countries to mark as selected.
 * @param onPick Invoked with the tapped country.
 */
@Composable
public fun CountryQuickPicks(
    picks: List<CountryQuickPick>,
    selectedCountries: Set<Country>,
    onPick: (Country) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = CountryPickerTheme.style.dimensions.groupHorizontalMargin),
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
) {
    if (picks.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        CapsLabel(
            text = stringResource(Res.string.ccp_section_quick_picks),
            style = style.typography.sectionLabel,
            color = style.colors.textSecondary,
            modifier = Modifier
                .padding(start = style.dimensions.groupHorizontalMargin + 4.dp, bottom = 8.dp)
                .semantics { heading() },
        )
        LazyRow(
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            // Room for the tiles' shadows, which a scrolling row would otherwise clip.
            modifier = Modifier.padding(vertical = 2.dp),
        ) {
            items(picks, key = { it.country.iso2Code }) { pick ->
                QuickPickTile(
                    pick = pick,
                    selected = pick.country in selectedCountries,
                    onClick = { onPick(pick.country) },
                    style = style,
                    flagContent = flagContent,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun QuickPickTile(
    pick: CountryQuickPick,
    selected: Boolean,
    onClick: () -> Unit,
    style: CountryPickerStyle,
    flagContent: (@Composable (Country) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = style.colors
    val shape = style.shapes.tile
    val country = pick.country
    val interaction = remember { MutableInteractionSource() }
    val scale by pressScale(interaction)
    val border by animateColorAsState(
        targetValue = if (selected) colors.accent else colors.hairline,
        animationSpec = style.motion.color,
        label = "tileBorder",
    )
    val borderWidth by animateDpAsState(if (selected) 1.5.dp else 0.75.dp, style.motion.dp, label = "tileBorderWidth")
    val check by animateFloatAsState(if (selected) 1f else 0f, style.motion.selection, label = "tileCheck")
    val detected = pick.source == QuickPickSource.Detected
    val detectedLabel = stringResource(Res.string.ccp_detected_badge)
    val description = stringResource(Res.string.ccp_a11y_row, country.displayName, country.dialCode)
        .let { if (detected) "$it. $detectedLabel" else it }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .width(style.dimensions.tileWidth)
                // A floor, not a fixed height: large text grows the tile instead of clipping it.
                .defaultMinSize(minHeight = style.dimensions.tileHeight)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .pickerShadow(style.elevation.tile, shape, colors.shadow)
                .clip(shape)
                .background(if (selected) colors.accentSoft else colors.surfaceRaised)
                .border(borderWidth, border, shape)
                .clickable(interactionSource = interaction, indication = null, role = Role.RadioButton, onClick = onClick)
                .semantics(mergeDescendants = true) {
                    contentDescription = description
                    this.selected = selected
                    role = Role.RadioButton
                }
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CountryFlag(
                country = country,
                size = style.dimensions.flagSizeTile,
                style = if (style.layout.flagStyle == CountryFlagStyle.Hidden) CountryFlagStyle.Circle else style.layout.flagStyle,
                flagContent = flagContent,
            )
            Spacer(Modifier.height(7.dp))
            Text(text = country.iso2Code, style = style.typography.tileLabel, color = colors.textPrimary, maxLines = 1)
            Text(
                text = country.dialCode,
                style = style.typography.caption.copy(textDirection = CountryPickerBidi.LTR),
                color = colors.textSecondary,
                maxLines = 1,
            )
        }

        // Corner marks sit half outside the tile, so they read as attached rather than as content.
        if (check > 0f) {
            CornerMark(style, Modifier.align(Alignment.TopEnd).graphicsLayer { scaleX = check; scaleY = check }) {
                AnimatedCheckMark(visible = selected, color = colors.onAccent, strokeWidth = 2.dp, modifier = Modifier.size(14.dp))
            }
        } else if (detected) {
            CornerMark(style, Modifier.align(Alignment.TopEnd)) {
                Icon(PickerIcons.LocationPin, contentDescription = null, tint = colors.onAccent, modifier = Modifier.size(11.dp))
            }
        }
    }
}

@Composable
private fun CornerMark(style: CountryPickerStyle, modifier: Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .offset(x = 5.dp, y = 3.dp)
            .size(20.dp)
            .background(style.colors.accent, CircleShape)
            .border(2.dp, style.colors.background, CircleShape),
        contentAlignment = Alignment.Center,
    ) { content() }
}
