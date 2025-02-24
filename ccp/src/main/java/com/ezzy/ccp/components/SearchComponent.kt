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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.icons.Close
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.icons.Search
import com.ezzy.ccp.state.SearchState

@Composable
fun SearchComponent(
    modifier: Modifier = Modifier,
    searchState: SearchState = SearchState(),
    onValueChange: (String) -> Unit = {},
    searchBorderWidth: Dp = 1.dp,
    searchBorderColor: Color = Color.Black.copy(alpha = .1f),
    searchCornerRadius: Dp = 16.dp,
    searchHint: String = "Search Countries",
    searchHintColor: Color = Color.Black.copy(alpha = .3f),
    searchHintStyle: TextStyle = TextStyle.Default,
    searchIconTint: Color = Color.Black.copy(alpha = .5f),
    searchBackgroundColor: Color = Color.White,
    searchTextColor: Color = Color.Black
) {

    val closeEnabled by remember {
        mutableStateOf(searchState.query.isNotBlank())
    }

    Surface(
        border = BorderStroke(searchBorderWidth, searchBorderColor),
        modifier = modifier,
        shape = RoundedCornerShape(searchCornerRadius),
        color = searchBackgroundColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Icon(
                imageVector = EzzyIcons.Search,
                contentDescription = "Search icon",
                tint = searchIconTint,
                modifier = Modifier.size(20.dp)
            )

            TextField(
                value = searchState.query,
                onValueChange = { password ->
                    onValueChange(password)
                },
                placeholder = {
                    Text(
                        text = searchHint,
                        style = searchHintStyle.copy(color = searchHintColor)
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
                    focusedTextColor = searchTextColor
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
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = EzzyIcons.Close,
                        contentDescription = "close icon",
                        tint = Color.Black,
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