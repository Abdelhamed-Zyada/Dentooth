package com.example.dentooth.core.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.auth.data.AuthRepository
import com.example.dentooth.core.auth.ui.LoginScreen
import com.example.dentooth.core.designsystem.components.DentoBrainLogo
import com.example.dentooth.core.localization.appStrings
import com.example.dentooth.core.navigation.MainScreen
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashScreen : Screen, KoinComponent {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authRepository by inject<AuthRepository>()
        val s = appStrings

        var startAnimation by remember { mutableStateOf(false) }
        val alphaAnim by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(durationMillis = 800)
        )
        val scaleAnim by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0.82f,
            animationSpec = tween(durationMillis = 800)
        )

        LaunchedEffect(Unit) {
            startAnimation = true
            delay(1600)
            val destination = if (authRepository.currentSession.value != null) {
                MainScreen()
            } else {
                LoginScreen()
            }
            navigator.replaceAll(destination)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Centered Branding: Logo + App Name + Tagline
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        alpha = alphaAnim
                        scaleX = scaleAnim
                        scaleY = scaleAnim
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DentoBrainLogo(
                    size = 110.dp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = s.appName,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = s.tagline,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom loading indicator & version
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .graphicsLayer { alpha = alphaAnim },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "v1.0 • ${s.appName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
