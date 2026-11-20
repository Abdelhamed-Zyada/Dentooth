package com.example.dentooth.feature.insurance.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
enum class InsuranceClaimStatus(val label: String, val colorHex: Long) {
    DRAFT("Draft Claim", 0xFF757575),
    SUBMITTED("Submitted to Insurer", 0xFF1976D2),
    UNDER_REVIEW("Under Review", 0xFFFB8C00),
    APPROVED("Approved by Insurer", 0xFF2E7D32),
    REJECTED("Claim Denied", 0xFFC62828),
    PAID_OUT("Disbursed / Paid", 0xFF388E3C);

    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
data class InsuranceCompany(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val portalUrl: String = "",
    val address: String = "Cairo, Egypt",
    val defaultCoveragePercent: Int = 80
)

@Serializable
data class PatientInsurancePolicy(
    val id: String,
    val patientId: String,
    val companyId: String,
    val companyName: String,
    val policyNumber: String,
    val groupNumber: String? = null,
    val coveragePercent: Int = 80,
    val annualMaxLimit: Double = 25000.0,
    val remainingBalance: Double = 18500.0,
    val validUntil: String = "2026-12-31"
)

@Serializable
data class InsuranceClaim(
    val id: String,
    val claimNumber: String,
    val patientId: String,
    val patientName: String,
    val invoiceId: String? = null,
    val invoiceNumber: String = "INV-2026-0001",
    val companyId: String,
    val companyName: String,
    val policyNumber: String,
    val totalAmount: Double,
    val coveredAmount: Double,
    val patientCopay: Double,
    val status: InsuranceClaimStatus = InsuranceClaimStatus.SUBMITTED,
    val dateSubmitted: String, // YYYY-MM-DD
    val approvalCode: String? = null,
    val notes: String? = null
)
