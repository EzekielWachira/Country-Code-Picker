package com.ezzy.ccp.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CountrySelector(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    selectedCountry: String = "",
    countriesToShow: List<String> = emptyList(), // listOf(US, UK, FR, KE ...etc)
    autoDetectCountry: Boolean = false,
    showHeader: Boolean = true
) {

}

@Composable
private fun CountryDisplayComponent (modifier: Modifier = Modifier) {

}