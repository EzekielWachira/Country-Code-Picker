package com.ezzy.ccp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.model.Country

@Composable
fun CountryItem(
    modifier: Modifier = Modifier,
    onClick: (Country) -> Unit = {},
    country: Country,
    showDialCode: Boolean = true,
    showFlag: Boolean = true,
    countryContainerColor: Color = Color.White,
    borderColor: Color = Color.Black,
    borderWidth: Dp = 1.dp,
    countryTextColor: Color = Color.Black,
    dialCodeTextColor: Color = Color.Black
) {
    Surface(
        color = countryContainerColor,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(borderWidth, borderColor),
        onClick = { onClick(country) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showFlag) {
                Icon(
                    imageVector = country.flag,
                    contentDescription = country.name + " flag",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = country.name,
                    color = countryTextColor,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (showDialCode) {
                    Text(
                        text = country.dialCode,
                        color = dialCodeTextColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CountryItemPreview() {
    CountryItem(country = countryList[0])
}