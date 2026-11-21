package com.example.dentooth.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnglishStrings }
val LocalAppLanguage = staticCompositionLocalOf<AppLanguage> { AppLanguage.ENGLISH }

/**
 * Convenience accessor for localized strings in Composable scope.
 * Usage: val s = appStrings
 *        Text(s.dashboardTitle)
 */
val appStrings: AppStrings
    @Composable
    @ReadOnlyComposable
    get() = LocalAppStrings.current
