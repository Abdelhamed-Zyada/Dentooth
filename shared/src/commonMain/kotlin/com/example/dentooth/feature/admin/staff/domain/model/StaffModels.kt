package com.example.dentooth.feature.admin.staff.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class StaffRole(val label: String, val colorHex: Long) {
    DOCTOR("Dental Specialist", 0xFF1976D2),
    DENTAL_ASSISTANT("Dental Assistant / Sterilization", 0xFF00796B),
    RECEPTIONIST("Front Desk / Patient Coordinator", 0xFF7B1FA2),
    ACCOUNTANT("Financial & Billing Officer", 0xFFE65100),
    CLINIC_MANAGER("Clinic Operations Manager", 0xFF388E3C);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
enum class AttendanceStatus(val label: String, val colorHex: Long) {
    PRESENT("Present", 0xFF2E7D32),
    LATE("Late Arrival", 0xFFFB8C00),
    ABSENT("Absent", 0xFFC62828),
    ON_LEAVE("Approved Leave", 0xFF546E7A);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
data class StaffMember(
    val id: String,
    val fullName: String,
    val role: StaffRole,
    val specialization: String? = null,
    val licenseNumber: String? = null,
    val phone: String,
    val email: String,
    val branchId: String = "branch-main",
    val branchName: String = "Main Clinic (New Cairo)",
    val hireDate: String, // YYYY-MM-DD
    val salary: Double = 0.0,
    val isPresentToday: Boolean = true,
    val activeCalendarColorHex: Long = 0xFF1976D2
)

@Serializable
data class AttendanceRecord(
    val id: String,
    val staffId: String,
    val staffName: String,
    val date: String, // YYYY-MM-DD
    val checkInTime: String? = null, // e.g. "08:50 AM"
    val checkOutTime: String? = null, // e.g. "05:00 PM"
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val notes: String? = null
)
