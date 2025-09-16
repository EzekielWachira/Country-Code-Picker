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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.icons.Close
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.icons.Search
import com.ezzy.ccp.model.CCPColors
import com.ezzy.ccp.model.CCPConfig
import com.ezzy.ccp.state.SearchState
import com.ezzy.ccp.utils.CCPDefaults

@Composable
fun SearchComponent(
    modifier: Modifier = Modifier,
    searchState: SearchState = SearchState(),
    onValueChange: (String) -> Unit = {},
    searchHint: String = "Search Countries",
    onClose: () -> Unit = {},
    ccpColors: CCPColors = CCPDefaults.colors(),
    ccpConfig: CCPConfig = CCPDefaults.defaultConfig()
) {

    val closeEnabled = searchState.query.isNotBlank()

    Surface(
        border = BorderStroke(
            ccpConfig.searchBorderWidth,
            ccpColors.ccpSheetColor.searchBorderColor
        ),
        modifier = modifier,
        shape = RoundedCornerShape(ccpConfig.searchCornerRadius),
        color = ccpColors.ccpSheetColor.containerColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = EzzyIcons.Search,
                contentDescription = "Search icon",
                tint = ccpColors.ccpSheetColor.searchIconTint,
                modifier = Modifier.size(20.dp)
            )

            TextField(
                value = searchState.query,
                onValueChange = { query ->
                    onValueChange(query)
                },
                placeholder = {
                    Text(
                        text = searchHint,
                        style = ccpConfig.searchHintStyle.copy(
                            color = ccpColors.ccpSheetColor.searchHintColor
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.inversePrimary,
                    focusedTextColor = ccpColors.ccpSheetColor.searchTextColor
                ),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Go
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                readOnly = false
            )

            AnimatedVisibility(closeEnabled, modifier = Modifier.offset(x = 10.dp)) {
                IconButton(onClick = {
                    if (searchState.query.isNotEmpty())
                        onValueChange("")
                    else
                        onClose()
                }) {
                    Icon(
                        imageVector = EzzyIcons.Close,
                        contentDescription = "close icon",
                        tint = ccpColors.ccpSheetColor.searchIconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SearchComponentPreview() {
    SearchComponent()
}