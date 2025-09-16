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

package com.ezzy.ccp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.components.PhoneNumberInput
import com.ezzy.ccp.model.SelectedCountry
import com.ezzy.ccp.ui.theme.CCPTheme
import com.ezzy.ccp.ui.theme.Wheat
import com.ezzy.ccp.utils.CCPDefaults

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var formatedPhone2 by remember { mutableStateOf("") }
            var unFormatedPhone2 by remember { mutableStateOf("") }
            var selectedCountry by remember { mutableStateOf<SelectedCountry?>(null) }
            var valid2 by remember { mutableStateOf(false) }
            var setPhone by remember { mutableStateOf<String?>("") }
            val testSetPhones = listOf(
                "+254712345678",  // Kenya
                "+14155552671",   // USA
                "+447911123456",  // UK
                "+919876543210",  // India
                "+819012345678",  // Japan
                "+4915123456789", // Germany
                "+33612345678",   // France
                "+61412345678",   // Australia
                "+5521987654321", // Brazil
                "+27821234567",   // South Africa
                "+34612345678",   // Spain
                "+393471234567",  // Italy
                "+79123456789",   // Russia
                "+8613800138000", // China
                "+971501234567",  // UAE
                "+966551234567",  // Saudi Arabia
                "+201001234567",  // Egypt
                "+48600123456",   // Poland
                "+46701234567",   // Sweden
                "+639123456789"   // Philippines
            )

            val testPhones2 = listOf(
                "254712345678",  // Kenya
                "14155552671",   // USA
                "447911123456",  // UK
                "919876543210",  // India
                "819012345678",  // Japan
                "4915123456789", // Germany
                "33612345678",   // France
                "61412345678",   // Australia
                "5521987654321", // Brazil
                "27821234567",   // South Africa
                "34612345678",   // Spain
                "393471234567",  // Italy
                "79123456789",   // Russia
                "8613800138000", // China
                "971501234567",  // UAE
                "966551234567",  // Saudi Arabia
                "201001234567",  // Egypt
                "48600123456",   // Poland
                "46701234567",   // Sweden
                "639123456789"   // Philippines
            )

            CCPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Wheat)
                            .padding(innerPadding)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            PhoneNumberInput(
//                                onPhoneValueChange = { formatedPhone, unFormatedPhone, valid ->
//                                    formatedPhone2 = formatedPhone
//                                    unFormatedPhone2 = unFormatedPhone
//                                    valid2 = valid
//                                },
                                value = setPhone.toString(),
                                setCountry = "KE",
                                onDone = {
                                    Toast.makeText(this@MainActivity, "On Done", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onValueChange = { (formattedPhone, phoneNumber, country, isValid) ->
                                    formatedPhone2 = formattedPhone
                                    unFormatedPhone2 = phoneNumber
                                    valid2 = isValid
                                    selectedCountry = country
                                },
                                ccpConfig = CCPDefaults.defaultConfig(
                                    showCountriesHeaderDivider = true,
                                    autoDetectCountry = true
                                )
                            )

                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "Formated Phone: $formatedPhone2",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "UnFormated Phone: $unFormatedPhone2",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "Country: ${selectedCountry?.name}",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Country Code: ${selectedCountry?.code}",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Country Dial Code: ${selectedCountry?.dialCode}",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Country Flag: ${selectedCountry?.flag}",
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            val validText = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Black
                                    )
                                ) {
                                    append("Valid: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = if (valid2) Color.Green else Color.Red
                                    )
                                ) {
                                    append(valid2.toString())
                                }
                            }
                            Text(
                                text = validText
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Button(onClick = {
                                setPhone = testSetPhones.random()
                            }) {
                                Text("Set Random Country Numbers")
                            }
                        }
                    }
                }
            }
        }
    }
}
