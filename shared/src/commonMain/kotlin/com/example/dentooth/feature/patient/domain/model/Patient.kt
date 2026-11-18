package com.example.dentooth.feature.patient.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
enum class Gender {
    MALE,
    FEMALE,
    OTHER;

    val displayName: String get() = when (this) {
        MALE -> "Male"
        FEMALE -> "Female"
        OTHER -> "Other"
    }
}

@Serializable
data class Patient(
    val id: String,
    val clinicId: String,
    val patientNumber: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val phoneSecondary: String? = null,
    val email: String? = null,
    val dateOfBirth: LocalDate? = null,
    val gender: Gender = Gender.MALE,
    val address: String? = null,
    val nationalId: String? = null,
    val bloodType: String? = null,
    val referredBy: String? = null,
    val notes: String? = null,
    val groups: List<String> = emptyList(),
    val totalBilled: Double = 0.0,
    val totalPaid: Double = 0.0,
    val lastVisitDate: LocalDate? = null,
    val isActive: Boolean = true
) {
    val fullName: String get() = "$firstName $lastName".trim()
    val balanceDue: Double get() = (totalBilled - totalPaid).coerceAtLeast(0.0)
}
