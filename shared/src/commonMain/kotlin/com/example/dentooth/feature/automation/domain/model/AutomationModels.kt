package com.example.dentooth.feature.automation.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class AutomationChannel(val label: String, val icon: String) {
    WHATSAPP("WhatsApp", "💬"),
    SMS("SMS Gateway", "📱"),
    INTERNAL_ALERT("In-App Alert", "🔔")
}

@Serializable
enum class AutomationRuleType(val label: String, val triggerDescription: String) {
    APPOINTMENT_REMINDER_24H(
        "24-Hour Appointment Confirmation",
        "Dispatched 24 hours prior to appointment time requesting confirmation."
    ),
    APPOINTMENT_REMINDER_2H(
        "2-Hour Arrival Reminder",
        "Dispatched 2 hours prior with branch location and GPS directions."
    ),
    POST_OP_CARE_FOLLOWUP(
        "Post-Operative Care Follow-up",
        "Dispatched 6 hours post surgical extraction, root canal, or implant placement."
    ),
    BIRTHDAY_GREETING(
        "Birthday Greeting & Special Offer",
        "Dispatched on patient's birthday with a celebratory voucher for checkup or hygiene."
    ),
    RECALL_DUE_6M(
        "6-Month Routine Hygiene Recall",
        "Dispatched to inactive patients who have not visited in 6 months for prophylaxis."
    ),
    LAB_READY_PATIENT_ALERT(
        "Crown / Prosthesis Ready Notice",
        "Dispatched automatically when dental lab order arrives and is checked in."
    ),
    PAYMENT_RECEIPT(
        "Instant E-Receipt Notification",
        "Dispatched immediately after payment settlement with a digital invoice link."
    )
}

@Serializable
enum class AutomationLogStatus(val label: String, val colorHex: Long) {
    SENT("Sent", 0xFF1976D2),
    DELIVERED("Delivered", 0xFF2E7D32),
    READ("Read", 0xFF00796B),
    FAILED("Failed", 0xFFC62828);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
data class AutomationRule(
    val id: String,
    val type: AutomationRuleType,
    val title: String,
    val description: String,
    val channel: AutomationChannel,
    val isEnabled: Boolean = true,
    val templateEnglish: String,
    val templateArabic: String,
    val totalSentCount: Int = 0,
    val lastTriggeredAt: String? = null
)

@Serializable
data class AutomationLog(
    val id: String,
    val ruleId: String,
    val ruleTitle: String,
    val patientId: String,
    val patientName: String,
    val patientPhone: String,
    val channel: AutomationChannel,
    val messageContent: String,
    val status: AutomationLogStatus,
    val timestamp: String,
    val costEstimateEgp: Double
)
