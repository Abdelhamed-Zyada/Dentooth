package com.example.dentooth.core.network.dto

import com.example.dentooth.feature.patient.domain.model.Gender
import com.example.dentooth.feature.patient.domain.model.Patient
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientSupabaseDto(
    val id: String,
    @SerialName("clinic_id") val clinicId: String = "cln-001",
    @SerialName("patient_number") val patientNumber: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val phone: String,
    @SerialName("phone_secondary") val phoneSecondary: String? = null,
    val email: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String = "MALE",
    val address: String? = null,
    @SerialName("national_id") val nationalId: String? = null,
    @SerialName("blood_type") val bloodType: String? = null,
    @SerialName("referred_by") val referredBy: String? = null,
    val notes: String? = null,
    val groups: List<String> = emptyList(),
    @SerialName("total_billed") val totalBilled: Double = 0.0,
    @SerialName("total_paid") val totalPaid: Double = 0.0,
    @SerialName("last_visit_date") val lastVisitDate: String? = null,
    @SerialName("is_active") val isActive: Boolean = true
) {
    fun toDomain(): Patient {
        return Patient(
            id = id,
            clinicId = clinicId,
            patientNumber = patientNumber,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            phoneSecondary = phoneSecondary,
            email = email,
            dateOfBirth = dateOfBirth?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            gender = when (gender.uppercase()) {
                "FEMALE" -> Gender.FEMALE
                "OTHER" -> Gender.OTHER
                else -> Gender.MALE
            },
            address = address,
            nationalId = nationalId,
            bloodType = bloodType,
            referredBy = referredBy,
            notes = notes,
            groups = groups,
            totalBilled = totalBilled,
            totalPaid = totalPaid,
            lastVisitDate = lastVisitDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
            isActive = isActive
        )
    }

    companion object {
        fun fromDomain(p: Patient): PatientSupabaseDto {
            return PatientSupabaseDto(
                id = p.id,
                clinicId = p.clinicId,
                patientNumber = p.patientNumber,
                firstName = p.firstName,
                lastName = p.lastName,
                phone = p.phone,
                phoneSecondary = p.phoneSecondary,
                email = p.email,
                dateOfBirth = p.dateOfBirth?.toString(),
                gender = p.gender.name,
                address = p.address,
                nationalId = p.nationalId,
                bloodType = p.bloodType,
                referredBy = p.referredBy,
                notes = p.notes,
                groups = p.groups,
                totalBilled = p.totalBilled,
                totalPaid = p.totalPaid,
                lastVisitDate = p.lastVisitDate?.toString(),
                isActive = p.isActive
            )
        }
    }
}
