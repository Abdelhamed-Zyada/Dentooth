package com.example.dentooth.feature.dentalchart.domain.model

import androidx.compose.ui.graphics.Color
import com.example.dentooth.core.designsystem.DentoothColors
import kotlinx.serialization.Serializable

@Serializable
enum class ToothSurface(val code: String, val displayName: String) {
    MESIAL("M", "Mesial"),
    DISTAL("D", "Distal"),
    BUCCAL("B", "Buccal / Facial"),
    LINGUAL("L", "Lingual / Palatal"),
    OCCLUSAL("O", "Occlusal / Incisal");

    companion object {
        fun fromCode(code: String): ToothSurface? =
            entries.find { it.code.equals(code, ignoreCase = true) }
    }
}

@Serializable
enum class ToothStatus(val displayName: String) {
    HEALTHY("Healthy"),
    CARIES("Caries / Cavity"),
    FILLED("Restored / Filled"),
    CROWN("Crown / Cap"),
    ROOT_CANAL("Root Canal Treated"),
    MISSING("Missing"),
    IMPLANT("Dental Implant"),
    BRIDGE_ABUTMENT("Bridge Abutment"),
    BRIDGE_PONTIC("Bridge Pontic"),
    IMPACTED("Impacted"),
    VENEER("Veneer");

    val color: Color get() = when (this) {
        HEALTHY -> DentoothColors.ToothHealthy
        CARIES -> DentoothColors.ToothCaries
        FILLED -> DentoothColors.ToothFilled
        CROWN -> DentoothColors.ToothCrown
        ROOT_CANAL -> DentoothColors.ToothRootCanal
        MISSING -> DentoothColors.ToothMissing
        IMPLANT -> DentoothColors.ToothImplant
        BRIDGE_ABUTMENT -> Color(0xFFFF9800)
        BRIDGE_PONTIC -> Color(0xFFFFB74D)
        IMPACTED -> Color(0xFF78909C)
        VENEER -> Color(0xFF81D4FA)
    }
}

@Serializable
data class ToothCondition(
    val id: String,
    val toothNumber: Int,
    val type: String, // e.g. "Caries", "Fracture", "Mobility Grade 1", "Wear"
    val surfaces: List<ToothSurface> = emptyList(),
    val severity: String = "Moderate",
    val notes: String? = null,
    val diagnosedAt: String? = null
)

@Serializable
data class ToothRecord(
    val toothNumber: Int,
    val status: ToothStatus = ToothStatus.HEALTHY,
    val affectedSurfaces: Set<ToothSurface> = emptySet(),
    val surfaceStatuses: Map<ToothSurface, ToothStatus> = emptyMap(),
    val conditions: List<ToothCondition> = emptyList(),
    val notes: String? = null,
    val isPrimary: Boolean = false // deciduous tooth
) {
    val toothName: String get() = getFdiToothName(toothNumber)

    fun getSurfaceColor(surface: ToothSurface): Color {
        val specific = surfaceStatuses[surface]
        if (specific != null) return specific.color
        if (affectedSurfaces.contains(surface)) return status.color
        if (status != ToothStatus.HEALTHY && surfaceStatuses.isEmpty() && affectedSurfaces.isEmpty()) {
            return status.color
        }
        return DentoothColors.ToothHealthy
    }

    companion object {
        fun getFdiToothName(fdi: Int): String {
            val quad = fdi / 10
            val tooth = fdi % 10
            val quadName = when (quad) {
                1 -> "Upper Right"
                2 -> "Upper Left"
                3 -> "Lower Left"
                4 -> "Lower Right"
                5 -> "Upper Right (Primary)"
                6 -> "Upper Left (Primary)"
                7 -> "Lower Left (Primary)"
                8 -> "Lower Right (Primary)"
                else -> "Tooth"
            }
            val toothType = when (tooth) {
                1 -> "Central Incisor"
                2 -> "Lateral Incisor"
                3 -> "Canine"
                4 -> "First Premolar"
                5 -> "Second Premolar"
                6 -> "First Molar"
                7 -> "Second Molar"
                8 -> "Third Molar (Wisdom)"
                else -> ""
            }
            return "$quadName $toothType (#$fdi)".trim()
        }
    }
}

@Serializable
data class DentalChart(
    val id: String,
    val patientId: String,
    val isPediatric: Boolean = false,
    val teeth: Map<Int, ToothRecord> = emptyMap()
) {
    fun getTooth(number: Int): ToothRecord =
        teeth[number] ?: ToothRecord(toothNumber = number)
}
