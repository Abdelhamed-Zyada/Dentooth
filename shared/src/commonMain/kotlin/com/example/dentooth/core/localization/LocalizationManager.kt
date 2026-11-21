package com.example.dentooth.core.localization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton state holder for in-app runtime language switching.
 */
object LocalizationManager {
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    val strings: AppStrings
        get() = if (_currentLanguage.value.isArabic) ArabicStrings else EnglishStrings

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.ENGLISH) {
            AppLanguage.ARABIC
        } else {
            AppLanguage.ENGLISH
        }
    }
}
