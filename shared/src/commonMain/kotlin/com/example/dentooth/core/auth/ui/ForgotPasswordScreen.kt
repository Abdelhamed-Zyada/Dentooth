package com.example.dentooth.core.auth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.components.DentoothFormField
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.core.localization.appStrings

class ForgotPasswordScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val s = appStrings
        var email by remember { mutableStateOf("") }
        var submitted by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                DentoothTopBar(
                    title = s.forgotPassword,
                    onBackClick = { navigator.pop() }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                Text(
                    text = s.resetLinkSentSuccess,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                DentoothFormField(
                    value = email,
                    onValueChange = { email = it },
                    label = s.emailLabel,
                    keyboardType = KeyboardType.Email,
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { submitted = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(if (submitted) s.success else s.sendResetLink)
                }
            }
        }
    }
}
