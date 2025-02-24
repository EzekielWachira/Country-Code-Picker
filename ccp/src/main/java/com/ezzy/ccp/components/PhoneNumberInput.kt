package com.ezzy.ccp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezzy.ccp.PhoneViewModel
import com.ezzy.ccp.data.countryList
import com.ezzy.ccp.icons.ChevronDown
import com.ezzy.ccp.icons.EzzyIcons
import com.ezzy.ccp.icons.UnitedStates
import com.ezzy.ccp.model.Country
import com.ezzy.ccp.utils.formatAndValidatePhone

@Composable
fun PhoneNumberInput(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    containerColor: Color = Color.White,
    cornerRadius: Dp = 16.dp,
    phoneHint: String = "Enter phone",
    phoneHintColor: Color = Color.Black,
    phoneHintStyle: TextStyle = TextStyle.Default,
    onPhoneValueChange: (formatedPhone: String, unFormatedPhone: String, valid: Boolean) -> Unit = { _, _, _ -> },
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    cursorColor: Color = Color.Black,
    inputTextColor: Color = Color.Black
) {

    var selectedCountry by remember { mutableStateOf<Country?>(null) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    val phoneState by remember {
        derivedStateOf {
            formatAndValidatePhone(phoneNumber.text, selectedCountry?.code ?: "US")
        }
    }
    val (formattedPhone, unformattedPhone, formattedWithoutCountryCode, isValid) = phoneState

    LaunchedEffect(formattedWithoutCountryCode) {
        phoneNumber = phoneNumber.copy(
            text = formattedWithoutCountryCode,
            selection = TextRange(formattedWithoutCountryCode.length)
        )
        onPhoneValueChange(formattedPhone, unformattedPhone, isValid)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = containerColor,
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectedCountryComponent(
                selectedCountry = selectedCountry ?: countryList.find { it.code.lowercase() == "us" },
                onSelectCountry = { country -> selectedCountry = country },
            )

            TextField(
                value = phoneNumber, // Use state variable
                onValueChange = { newValue ->
                    phoneNumber = newValue.copy(
                        text = newValue.text,
                        selection = TextRange(newValue.text.length) // Ensure cursor is at end
                    )
                    onValueChange(newValue.text)
                },
                placeholder = {
                    Text(
                        text = phoneHint,
                        style = phoneHintStyle,
                        color = phoneHintColor
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = cursorColor,
                    focusedTextColor = inputTextColor,
                ),
                modifier = Modifier
                    .weight(1f)
                    .offset(x = (-25).dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Go
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                readOnly = false
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedCountryComponent(
    modifier: Modifier = Modifier,
    selectedCountry: Country? = countryList.find { it.code == "US" },
    viewModel: PhoneViewModel = viewModel(),
    onSelectCountry: (Country) -> Unit = {},
    searchTextColor: Color = Color.Black
) {

    var isCountryBottomSheetVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(true) {
        val countryCodeLocale = context.resources.configuration.locales[0].country
        viewModel.setCountryForLocale(countryCodeLocale)
    }

    Surface(
        modifier = modifier, onClick = {
            isCountryBottomSheetVisible = true
        }, shape = MaterialTheme.shapes.small, color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp, vertical = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = selectedCountry?.flag ?: EzzyIcons.UnitedStates,
                contentDescription = selectedCountry?.name + " flag",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = selectedCountry?.dialCode ?: "00",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = EzzyIcons.ChevronDown,
                contentDescription = "down arrow",
                tint = Color.Black.copy(alpha = .5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (isCountryBottomSheetVisible) {
        CountriesBottomSheet(sheetState = rememberModalBottomSheetState(true), onDismiss = {
            isCountryBottomSheetVisible = false
        }, onSelectCountries = {
            isCountryBottomSheetVisible = false
            onSelectCountry(it)
        }, cornerRadius = 0.dp,
            searchTextColor= searchTextColor
        )
    }
}

@Preview
@Composable
private fun PhoneFieldComponentPreview() {
    PhoneNumberInput()
}

@Preview
@Composable
private fun SelectedCountryComponentPreview() {
    SelectedCountryComponent(selectedCountry = countryList[0])
}