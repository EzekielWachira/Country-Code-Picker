package com.ezzy.ccp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ezzy.ccp.components.PhoneFieldComponent
import com.ezzy.ccp.ui.theme.CCPTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var formatedPhone2 by remember { mutableStateOf("") }
            var unFormatedPhone2 by remember { mutableStateOf("") }
            var valid2 by remember { mutableStateOf(false) }

            CCPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            PhoneFieldComponent(
                                onPhoneValueChange = { formatedPhone, unFormatedPhone, valid ->
                                    formatedPhone2 = formatedPhone
                                    unFormatedPhone2 = unFormatedPhone
                                    valid2 = valid
                                }
                            )

                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "Formated Phone: $formatedPhone2"
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "UnFormated Phone: $unFormatedPhone2"
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "Valid: $valid2"
                            )
                        }
                    }
                }
            }
        }
    }
}
