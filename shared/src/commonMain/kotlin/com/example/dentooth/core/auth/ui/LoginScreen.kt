package com.example.dentooth.core.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.components.DentoBrainLogo
import com.example.dentooth.core.designsystem.components.DentoothFormField
import com.example.dentooth.core.localization.appStrings
import com.example.dentooth.core.localization.ui.LanguageToggleButton
import com.example.dentooth.core.navigation.MainScreen

class LoginScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<LoginViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val s = appStrings

        var email by remember { mutableStateOf("dr.ahmed@dentobrain.app") }
        var password by remember { mutableStateOf("password123") }

        LaunchedEffect(uiState) {
            if (uiState.isSuccess) {
                navigator.replaceAll(MainScreen())
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Language Switcher Toggle Pill
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    LanguageToggleButton()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary App Logo & DentoBrain Branding
                DentoBrainLogo(
                    size = 96.dp,
                    showWordmark = false,
                    showTagline = false
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = s.appName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = s.tagline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email Field
                DentoothFormField(
                    value = email,
                    onValueChange = {
                        email = it
                        viewModel.clearError()
                    },
                    label = s.emailLabel,
                    keyboardType = KeyboardType.Email,
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                DentoothFormField(
                    value = password,
                    onValueChange = {
                        password = it
                        viewModel.clearError()
                    },
                    label = s.passwordLabel,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    }
                )

                if (uiState is com.example.dentooth.core.util.UiState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (uiState as com.example.dentooth.core.util.UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                Button(
                    onClick = { viewModel.signIn(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = s.signInButton,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navigator.push(ForgotPasswordScreen()) }
                ) {
                    Text(
                        text = s.forgotPassword,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
