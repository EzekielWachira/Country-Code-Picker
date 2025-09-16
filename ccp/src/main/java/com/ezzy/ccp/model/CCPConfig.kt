package com.ezzy.ccp.model

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

data class CCPConfig(
    val phoneInputCornerRadius: Dp,
    val phoneHintStyle: TextStyle,
    val borderWidth: Dp,
    val autoDetectCountry: Boolean,
    val showHeader: Boolean,
    val showCountryFlag: Boolean,
    val countriesSheetShape: Shape,
    val searchBorderWidth: Dp,
    val searchCornerRadius: Dp,
    val searchHintStyle: TextStyle,
    val headerStyle: TextStyle,
    val showDialCodeCountryItem: Boolean,
    val showFlagCountryItem: Boolean,
    val countryItemDialCodeTextStyle: TextStyle,
    val countryItemNameTextStyle: TextStyle,
    val countryItemShape: Shape,
    val phoneInputShape: Shape,
    val showCountriesHeaderDivider: Boolean,
)
