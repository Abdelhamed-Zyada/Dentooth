package com.example.dentooth.feature.treatment.domain.model

import androidx.compose.ui.graphics.Color
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.feature.dentalchart.domain.model.ToothSurface
import kotlinx.serialization.Serializable

@Serializable
enum class PlanStatus(val displayName: String) {
    PLANNED("Planned"),
    ACTIVE("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    val color: Color get() = when (this) {
        PLANNED -> Color(0xFF78909C)
        ACTIVE -> DentoothColors.Primary
        COMPLETED -> DentoothColors.Secondary
        CANCELLED -> DentoothColors.Error
    }
}

@Serializable
enum class ProcedureStatus(val displayName: String) {
    PLANNED("Planned"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    val color: Color get() = when (this) {
        PLANNED -> Color(0xFF78909C)
        IN_PROGRESS -> DentoothColors.Tertiary
        COMPLETED -> DentoothColors.Secondary
        CANCELLED -> DentoothColors.Error
    }
}

@Serializable
enum class ProcedureCategory(val displayName: String) {
    DIAGNOSTIC("Diagnostic & Exam"),
    PREVENTIVE("Preventive & Hygiene"),
    RESTORATIVE("Restorative / Fillings"),
    ENDODONTIC("Endodontics / Root Canal"),
    PROSTHODONTIC("Prosthodontics / Crowns"),
    SURGICAL("Oral Surgery / Extraction"),
    PERIODONTIC("Periodontics / Gum"),
    ORTHODONTIC("Orthodontics / Braces")
}

@Serializable
data class ProcedureCatalogItem(
    val id: String,
    val code: String,
    val name: String,
    val nameAr: String? = null,
    val category: ProcedureCategory,
    val defaultCost: Double,
    val estimatedDurationMinutes: Int = 30,
    val description: String? = null
)

@Serializable
data class PlanProcedure(
    val id: String,
    val planId: String,
    val toothNumber: Int? = null,
    val procedureName: String,
    val category: ProcedureCategory = ProcedureCategory.RESTORATIVE,
    val cost: Double,
    val quantity: Int = 1,
    val status: ProcedureStatus = ProcedureStatus.PLANNED,
    val surfaces: List<ToothSurface> = emptyList(),
    val notes: String? = null,
    val completedAt: String? = null
) {
    val totalCost: Double get() = cost * quantity
}

@Serializable
data class TreatmentPlan(
    val id: String,
    val patientId: String,
    val doctorName: String = "Dr. Ahmed Hassan",
    val title: String,
    val status: PlanStatus = PlanStatus.PLANNED,
    val procedures: List<PlanProcedure> = emptyList(),
    val discount: Double = 0.0,
    val notes: String? = null,
    val createdAt: String? = null
) {
    val subtotal: Double get() = procedures.filter { it.status != ProcedureStatus.CANCELLED }.sumOf { it.totalCost }
    val totalCost: Double get() = (subtotal - discount).coerceAtLeast(0.0)
    val completedProceduresCount: Int get() = procedures.count { it.status == ProcedureStatus.COMPLETED }
    val progress: Float get() = if (procedures.isEmpty()) 0f else completedProceduresCount.toFloat() / procedures.size
}
