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

package com.ezzy.ccp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.utils.CCPDefaults
import com.ezzy.ccp.utils.countryToFlagEmoji

/** Full-width list item showing flag, name, and dial code. Used in list layout mode. */
@Composable
fun CountryItem(
    modifier: Modifier = Modifier,
    onClick: (Country) -> Unit = {},
    country: Country,
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig()
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        color = ccpColors.ccpSheetColor.countryItemContainerColor,
        modifier = modifier.semantics {
            contentDescription = "${country.name}, ${country.dialCode}"
            role = Role.Button
        },
        shape = ccpConfig.countryItemShape,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick(country)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (ccpConfig.showFlagCountryItem) {
                Text(
                    text = country.code.countryToFlagEmoji() ?: "",
                    fontSize = 22.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = country.name,
                    color = ccpColors.ccpSheetColor.countryItemTextColor,
                    style = ccpConfig.countryItemNameTextStyle
                )
                if (ccpConfig.showDialCodeCountryItem) {
                    Text(
                        text = country.dialCode,
                        color = ccpColors.ccpSheetColor.countryItemDialCodeTextColor,
                        style = ccpConfig.countryItemDialCodeTextStyle,
                    )
                }
            }
        }
    }
}

/** Compact grid cell showing flag, ISO code, and dial code. Used in grid layout mode. */
@Composable
fun CountryGridItem(
    modifier: Modifier = Modifier,
    onClick: (Country) -> Unit = {},
    country: Country,
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig()
) {
    val haptic = LocalHapticFeedback.current
    Surface(
        color = ccpColors.ccpSheetColor.countryItemContainerColor,
        modifier = modifier.semantics {
            contentDescription = "${country.name}, ${country.dialCode}"
            role = Role.Button
        },
        shape = ccpConfig.countryItemShape,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick(country)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = country.code.countryToFlagEmoji() ?: "",
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = country.code,
                color = ccpColors.ccpSheetColor.countryItemTextColor,
                style = ccpConfig.countryItemNameTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = country.dialCode,
                color = ccpColors.ccpSheetColor.countryItemDialCodeTextColor,
                style = ccpConfig.countryItemDialCodeTextStyle,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun CountryItemPreview() {
    CountryItem(country = countryList[0])
}

@Preview
@Composable
private fun CountryGridItemPreview() {
    CountryGridItem(country = countryList[0])
}
