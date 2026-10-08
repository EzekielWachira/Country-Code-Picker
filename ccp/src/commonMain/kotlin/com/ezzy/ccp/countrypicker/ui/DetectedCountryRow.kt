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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.countrypicker.detection.CountryDetectionSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.theme.CountryPickerStyle
import com.ezzy.ccp.countrypicker.theme.CountryPickerTheme
import com.ezzy.ccp.resources.Res
import com.ezzy.ccp.resources.ccp_detect_source_locale
import com.ezzy.ccp.resources.ccp_detect_source_network
import com.ezzy.ccp.resources.ccp_detect_source_sim
import com.ezzy.ccp.resources.ccp_detected_badge
import com.ezzy.ccp.resources.ccp_detected_suggestion
import com.ezzy.ccp.resources.ccp_use
import org.jetbrains.compose.resources.stringResource

/**
 * "📍 Detected · From your SIM card · tap to change" — shown under a field whose value came from
 * detection rather than from the user.
 *
 * The source is what makes this honest: "from your SIM card" is a claim a user can evaluate, where a
 * bare "Detected" is not. The badge and source are announced as one sentence.
 */
@Composable
public fun DetectedCountryBadge(
    source: CountryDetectionSource,
    modifier: Modifier = Modifier,
    style: CountryPickerStyle = CountryPickerTheme.style,
) {
    val badgeText = stringResource(Res.string.ccp_detected_badge)
    val sourceText = source.label.resolve()
    Row(
        modifier = modifier.semantics { contentDescription = "$badgeText. $sourceText" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PickerBadge(
            text = badgeText,
            container = style.colors.accentSoft,
            content = style.colors.onAccentSoft,
            icon = PickerIcons.LocationPin,
            modifier = Modifier.clearAndSetSemantics {},
        )
        SingleLineText(
            text = sourceText,
            style = style.typography.helper,
            color = style.colors.textSecondary,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}

/**
 * A banner offering the detected country — "Looks like you're in Kenya, from your SIM card." — with
 * a one-tap Use action, for when detection should suggest rather than decide.
 *
 * @param visible Animates the banner in and out.
 */
@Composable
public fun DetectedCountrySuggestion(
    country: Country,
    source: CountryDetectionSource,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    style: CountryPickerStyle = CountryPickerTheme.style,
    flagContent: (@Composable (Country) -> Unit)? = null,
) {
    val colors = style.colors
    val shape = RoundedCornerShape(style.shapes.groupCornerRadius)
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(style.motion.fadeIn) + expandVertically(style.motion.size),
        exit = fadeOut(style.motion.fadeOut) + shrinkVertically(style.motion.size),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pickerShadow(style.elevation.card, shape, colors.shadow)
                .background(colors.surfaceRaised, shape)
                .border(0.75.dp, colors.hairline, shape)
                .padding(start = 12.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box {
                CountryFlag(country = country, size = style.dimensions.flagSizeRow, flagContent = flagContent)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .background(colors.accent, CircleShape)
                        .border(2.dp, colors.surfaceRaised, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(PickerIcons.LocationPin, contentDescription = null, tint = colors.onAccent, modifier = Modifier.size(10.dp))
                }
            }
            Text(
                text = stringResource(Res.string.ccp_detected_suggestion, country.displayName, source.detectionSourceName()),
                style = style.typography.helper,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            PickerButton(
                onClick = onAccept,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(stringResource(Res.string.ccp_use))
            }
        }
    }
}

@Composable
private fun CountryDetectionSource.detectionSourceName(): String = stringResource(
    when (this) {
        CountryDetectionSource.Sim -> Res.string.ccp_detect_source_sim
        CountryDetectionSource.Network -> Res.string.ccp_detect_source_network
        else -> Res.string.ccp_detect_source_locale
    },
)
