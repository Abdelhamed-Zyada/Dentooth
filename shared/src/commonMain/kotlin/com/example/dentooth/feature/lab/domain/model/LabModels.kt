package com.example.dentooth.feature.lab.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class LabOrderStatus(val label: String, val colorHex: Long) {
    PENDING("Pending Dispatch", 0xFF757575),
    SENT_TO_LAB("Sent to Lab", 0xFF1976D2),
    IN_FABRICATION("In Fabrication", 0xFFFB8C00),
    READY_FOR_DELIVERY("Ready for Delivery", 0xFF00796B),
    DELIVERED("Delivered to Clinic", 0xFF2E7D32),
    FITTED("Fitted on Patient", 0xFF43A047),
    REMAKE_REQUESTED("Remake Requested", 0xFFC62828);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
enum class RestorationType(val label: String, val typicalDays: Int) {
    ZIRCONIA_CROWN("Zirconia Monolithic Crown", 4),
    EMAX_VENEER("E.max Aesthetic Veneer", 3),
    PFM_CROWN("Porcelain Fused to Metal (PFM)", 5),
    IMPLANT_CUSTOM_ABUTMENT("Custom Titanium Abutment + Crown", 6),
    REMOVABLE_PARTIAL_DENTURE("Cast Cobalt-Chrome RPD", 7),
    CLEAR_ALIGNER_TRAY("Thermoformed Orthodontic Retainer", 2),
    NIGHT_GUARD("Occlusal Night Guard / Splint", 3)
}

@Serializable
data class DentalLab(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val address: String,
    val averageTurnaroundDays: Int = 4,
    val rating: Double = 4.8
)

@Serializable
data class LabOrder(
    val id: String,
    val orderNumber: String,
    val patientId: String,
    val patientName: String,
    val doctorName: String = "Dr. Ahmed Mostafa",
    val labId: String,
    val labName: String,
    val restorationType: RestorationType = RestorationType.ZIRCONIA_CROWN,
    val toothNumber: Int? = null,
    val shade: String = "A2", // e.g. "A1", "A2", "A3.5", "BL1"
    val orderDate: String,     // YYYY-MM-DD
    val expectedDeliveryDate: String, // YYYY-MM-DD
    val actualDeliveryDate: String? = null,
    val cost: Double = 0.0,    // Lab fee in EGP
    val status: LabOrderStatus = LabOrderStatus.SENT_TO_LAB,
    val notes: String? = null,
    val trackingReference: String? = null
)
