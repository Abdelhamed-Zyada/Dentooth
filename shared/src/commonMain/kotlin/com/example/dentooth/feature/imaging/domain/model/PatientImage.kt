package com.example.dentooth.feature.imaging.domain.model

import androidx.compose.ui.graphics.Color
import com.example.dentooth.core.designsystem.DentoothColors
import kotlinx.serialization.Serializable

@Serializable
enum class ImageType(val displayName: String) {
    OPG_PANORAMIC("Panoramic OPG"),
    PERIAPICAL_XRAY("Periapical PA"),
    BITEWING_XRAY("Bitewing"),
    INTRAORAL_PHOTO("Intraoral Photo"),
    EXTRAORAL_PHOTO("Extraoral Photo"),
    CBCT_SCAN("CBCT Scan");

    val badgeColor: Color get() = when (this) {
        OPG_PANORAMIC -> DentoothColors.Primary
        PERIAPICAL_XRAY -> DentoothColors.StatusScheduled
        BITEWING_XRAY -> Color(0xFF00897B)
        INTRAORAL_PHOTO -> DentoothColors.Secondary
        EXTRAORAL_PHOTO -> DentoothColors.Tertiary
        CBCT_SCAN -> Color(0xFF8E24AA)
    }
}

@Serializable
data class PatientImage(
    val id: String,
    val patientId: String,
    val toothNumber: Int? = null,
    val type: ImageType,
    val title: String,
    val date: String,
    val imageUrl: String = "",
    val notes: String? = null
)
