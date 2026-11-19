package com.example.dentooth.feature.calendar.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class AppointmentStatus(val label: String, val colorHex: Long) {
    SCHEDULED("Scheduled", 0xFF2196F3),
    CONFIRMED("Confirmed", 0xFF00897B),
    IN_PROGRESS("In Progress", 0xFFFB8C00),
    COMPLETED("Completed", 0xFF43A047),
    CANCELLED("Cancelled", 0xFFE53935),
    NO_SHOW("No Show", 0xFF757575);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
enum class AppointmentType(val label: String, val defaultDurationMinutes: Int) {
    CONSULTATION("Consultation", 30),
    TREATMENT("Treatment", 60),
    FOLLOW_UP("Follow-up", 30),
    EMERGENCY("Emergency", 45),
    CHECK_UP("Routine Check-up", 30)
}

@Serializable
data class Chair(
    val id: String,
    val branchId: String = "branch-main",
    val name: String,
    val isActive: Boolean = true
)

@Serializable
data class StaffDoctor(
    val id: String,
    val name: String,
    val specialization: String,
    val colorHex: Long
) {
    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
data class Appointment(
    val id: String,
    val clinicId: String = "clinic-main",
    val branchId: String = "branch-main",
    val patientId: String,
    val patientName: String,
    val patientPhone: String = "",
    val doctorId: String,
    val doctorName: String,
    val chairId: String? = null,
    val chairName: String? = null,
    val planProcedureId: String? = null,
    val procedureName: String? = null,
    val toothNumber: Int? = null,
    val title: String,
    val date: String,             // Format: YYYY-MM-DD
    val startTime: String,        // Format: HH:mm (e.g. "10:30")
    val endTime: String,          // Format: HH:mm (e.g. "11:30")
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val type: AppointmentType = AppointmentType.TREATMENT,
    val notes: String? = null,
    val reminderSent: Boolean = false,
    val customColorHex: Long? = null
) {
    val durationMinutes: Int
        get() {
            return try {
                val (sh, sm) = startTime.split(":").map { it.toInt() }
                val (eh, em) = endTime.split(":").map { it.toInt() }
                (eh * 60 + em) - (sh * 60 + sm)
            } catch (e: Exception) {
                type.defaultDurationMinutes
            }
        }

    val displayColor: Color
        get() = customColorHex?.let { Color(it) } ?: status.composeColor
}

data class AppointmentConflict(
    val hasConflict: Boolean,
    val message: String? = null,
    val conflictingAppointment: Appointment? = null
)
