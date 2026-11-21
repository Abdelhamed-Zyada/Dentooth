package com.example.dentooth.core.config

/**
 * Global Application & Brand Configuration Attributes.
 * Changing attributes here updates the app branding across the entire codebase.
 */
object AppConfig {
    var appName: String = "DentoBrain"
    var appNameArabic: String = "دنتوبرين"
    
    var tagline: String = "The brain behind your dental practice"
    var taglineArabic: String = "عقل عيادتك الذكي"
    
    var defaultEmail: String = "contact@dentobrain.app"
    var defaultWebsite: String = "https://dentobrain.app"
    
    var defaultCurrency: String = "EGP"
    var defaultCurrencyArabic: String = "ج.م"
    
    var emergencyPhone: String = "19888"
    var version: String = "1.0.0"

    fun getLocalizedAppName(isArabic: Boolean): String = if (isArabic) appNameArabic else appName
    fun getLocalizedTagline(isArabic: Boolean): String = if (isArabic) taglineArabic else tagline
    fun getLocalizedCurrency(isArabic: Boolean): String = if (isArabic) defaultCurrencyArabic else defaultCurrency
}
