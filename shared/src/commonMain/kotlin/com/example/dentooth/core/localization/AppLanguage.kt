package com.example.dentooth.core.localization

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val isRtl: Boolean
) {
    ENGLISH("en", "English", "English", isRtl = false),
    ARABIC("ar", "Arabic", "العربية", isRtl = true);

    val isArabic: Boolean get() = this == ARABIC
}
