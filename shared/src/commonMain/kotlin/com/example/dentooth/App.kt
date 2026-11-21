package com.example.dentooth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.example.dentooth.core.designsystem.DentoothTheme
import com.example.dentooth.core.di.appModule
import com.example.dentooth.core.localization.ArabicStrings
import com.example.dentooth.core.localization.EnglishStrings
import com.example.dentooth.core.localization.LocalAppLanguage
import com.example.dentooth.core.localization.LocalAppStrings
import com.example.dentooth.core.localization.LocalizationManager
import com.example.dentooth.core.navigation.MainScreen
import org.koin.compose.KoinApplication

@Composable
fun App() {
    val currentLanguage by LocalizationManager.currentLanguage.collectAsState()
    val activeStrings = if (currentLanguage.isArabic) ArabicStrings else EnglishStrings
    val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    KoinApplication(application = {
        modules(appModule)
    }) {
        CompositionLocalProvider(
            LocalAppLanguage provides currentLanguage,
            LocalAppStrings provides activeStrings,
            LocalLayoutDirection provides layoutDirection
        ) {
            DentoothTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Navigator(MainScreen()) { navigator ->
                        SlideTransition(navigator)
                    }
                }
            }
        }
    }
}
