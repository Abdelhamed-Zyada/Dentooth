package com.example.dentooth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.example.dentooth.core.designsystem.DentoothTheme
import com.example.dentooth.core.di.appModule
import com.example.dentooth.core.navigation.MainScreen
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        DentoothTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Navigator(MainScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}
