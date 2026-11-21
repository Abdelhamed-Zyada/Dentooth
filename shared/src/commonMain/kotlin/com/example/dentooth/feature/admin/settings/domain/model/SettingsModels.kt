package com.example.dentooth.feature.admin.settings.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ToothNotationSystem(val label: String, val description: String) {
    FDI("FDI Two-Digit (World Dental Federation)", "Standard international 11-48 notation used in Egypt & Middle East"),
    UNIVERSAL("Universal Numbering System (1-32)", "Numbered 1 to 32 sequentially from upper right molar to lower right molar")
}

@Serializable
data class ClinicSettings(
    val clinicName: String = "DentoBrain Dental Centers",
    val phone: String = "+20228192031",
    val email: String = "contact@dentobrain.app",
    val website: String = "https://dentobrain.app",
    val address: String = "South 90th Street, Medical Park Premier, New Cairo",
    val taxRegistrationNumber: String = "492-108-320",
    val commercialLicense: String = "D-2024-CAIRO",
    val currency: String = "EGP",
    val toothNotation: ToothNotationSystem = ToothNotationSystem.FDI,
    val defaultAppointmentMinutes: Int = 45,
    val reminderTimingHours: Int = 24,
    val smsAutomationEnabled: Boolean = true,
    val whatsappAutomationEnabled: Boolean = true,
    val autoSendLabOnTreatment: Boolean = true
)
