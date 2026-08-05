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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.R
import com.ezzy.ccp.countrypicker.detection.CountryDetectionSource
import com.ezzy.ccp.countrypicker.model.Country
import com.ezzy.ccp.countrypicker.model.resolve
import com.ezzy.ccp.countrypicker.theme.CountryFlagShape
import com.ezzy.ccp.countrypicker.theme.CountryPickerColors
import com.ezzy.ccp.countrypicker.theme.CountryPickerDefaults
import com.ezzy.ccp.countrypicker.theme.CountryPickerDimensions
import com.ezzy.ccp.countrypicker.theme.CountryPickerMotion
import com.ezzy.ccp.countrypicker.theme.CountryPickerShapes
import com.ezzy.ccp.countrypicker.theme.CountryPickerTypography

/**
 * The "✓ Detected — From your network · tap to change" supporting row.
 *
 * Pass this as `supportingText` content beneath a [CountrySelector] when
 * [com.ezzy.ccp.countrypicker.detection.CountryDetectionBehavior.ShowBadge] is in effect.
 *
 * The badge is tonal rather than a saturated accent: it is an explanation of where a value came from,
 * not a call to action, and styling it loudly makes users think something needs fixing. Naming the
 * *source* ("from your SIM card") rather than just claiming "detected" is what lets a user judge whether
 * to trust it — someone roaming abroad knows their SIM country is not where they live.
 */
@Composable
fun DetectedCountryBadge(
    source: CountryDetectionSource,
    modifier: Modifier = Modifier,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
) {
    val badgeText = stringResource(R.string.ccp_detected_badge)
    val sourceText = source.label.resolve()

    Row(
        // One description for the whole row: TalkBack reads "Detected. From your SIM card · tap to
        // change." as a sentence rather than three fragments.
        modifier = modifier.semantics {
            contentDescription = "$badgeText. $sourceText"
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BADGE_SPACING),
    ) {
        Row(
            modifier = Modifier
                .clip(shapes.detectedBadge)
                .background(colors.detectedBadgeContainer)
                .padding(horizontal = BADGE_HORIZONTAL_PADDING, vertical = BADGE_VERTICAL_PADDING)
                .clearAndSetSemantics {},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(BADGE_ICON_SPACING),
        ) {
            Icon(
                imageVector = PickerIcons.Check,
                contentDescription = null,
                tint = colors.detectedBadgeContent,
                modifier = Modifier.size(BADGE_ICON_SIZE),
            )
            Text(
                text = badgeText,
                style = typography.badgeLabel,
                color = colors.detectedBadgeContent,
            )
        }

        Text(
            text = sourceText,
            style = typography.helperText,
            color = colors.selectorSecondaryContent,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}

/**
 * The ask-first suggestion card: "Looks like you're in Germany, from your network." with a Use action.
 *
 * Used with [com.ezzy.ccp.countrypicker.detection.CountryDetectionBehavior.AskFirst], where detection
 * must not fill the field on its own. That is the right default for fields with legal weight — tax
 * residency, sanctions screening — because a silently prefilled answer the user never actually gave is a
 * liability rather than a convenience.
 */
@Composable
fun DetectedCountrySuggestion(
    country: Country,
    source: CountryDetectionSource,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    flagShape: CountryFlagShape = CountryFlagShape.Circle,
    colors: CountryPickerColors = CountryPickerDefaults.colors(),
    shapes: CountryPickerShapes = CountryPickerDefaults.shapes(),
    dimensions: CountryPickerDimensions = CountryPickerDefaults.dimensions(),
    typography: CountryPickerTypography = CountryPickerDefaults.typography(),
    motion: CountryPickerMotion = CountryPickerDefaults.motion(),
    flagContent: (@Composable (Country) -> Unit)? = null,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(motion.fadeIn),
        exit = fadeOut(motion.fadeOut),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(top = SUGGESTION_TOP_PADDING)
                .clip(shapes.currentSelectionCard)
                .background(colors.currentSelectionContainer)
                .padding(SUGGESTION_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SUGGESTION_SPACING),
        ) {
            CountryFlag(
                country = country,
                size = dimensions.flagSizeRow,
                shape = flagShape,
                colors = colors,
                dimensions = dimensions,
                motion = motion,
                flagContent = flagContent,
            )
            Text(
                text = stringResource(
                    R.string.ccp_detected_suggestion,
                    country.displayName,
                    source.detectionSourceName(),
                ),
                style = typography.sheetSubtitle,
                color = colors.sheetContent,
                modifier = Modifier.weight(1f),
            )
            Button(
                onClick = onAccept,
                shape = shapes.button,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.selectedRowContainer,
                    contentColor = colors.selectedRowContent,
                ),
            ) {
                Text(text = stringResource(R.string.ccp_use), style = typography.buttonLabel)
            }
        }
    }
}

/**
 * The mid-sentence name of a detection source: "your SIM card", "your network", "your device language".
 *
 * Separate from [CountryDetectionSource.label] because that one is a standalone phrase
 * ("From your SIM card · tap to change") and this one has to read correctly inside "Looks like you're in
 * Germany, from ___." Reusing one string for both produces broken sentences in at least one of them.
 */
@Composable
private fun CountryDetectionSource.detectionSourceName(): String = stringResource(
    when (this) {
        CountryDetectionSource.Sim -> R.string.ccp_detect_source_sim
        CountryDetectionSource.Network -> R.string.ccp_detect_source_network
        else -> R.string.ccp_detect_source_locale
    },
)

private val BADGE_SPACING = 8.dp
private val BADGE_ICON_SPACING = 5.dp
private val BADGE_ICON_SIZE = 14.dp
private val BADGE_HORIZONTAL_PADDING = 8.dp
private val BADGE_VERTICAL_PADDING = 3.dp
private val SUGGESTION_TOP_PADDING = 8.dp
private val SUGGESTION_PADDING = 12.dp
private val SUGGESTION_SPACING = 12.dp
