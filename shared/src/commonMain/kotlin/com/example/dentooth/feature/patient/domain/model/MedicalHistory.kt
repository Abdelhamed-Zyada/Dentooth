package com.example.dentooth.feature.patient.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MedicalHistory(
    val id: String,
    val patientId: String,
    val hasDiabetes: Boolean = false,
    val hasHypertension: Boolean = false,
    val hasHeartDisease: Boolean = false,
    val hasHepatitis: Boolean = false,
    val hasHiv: Boolean = false,
    val hasAsthma: Boolean = false,
    val hasBleedingDisorder: Boolean = false,
    val isPregnant: Boolean = false,
    val isBreastfeeding: Boolean = false,
    val isSmoker: Boolean = false,
    val allergies: String = "",
    val currentMedications: String = "",
    val previousSurgeries: String = "",
    val otherConditions: String = "",
    val lastUpdated: String? = null
) {
    val hasAnyCondition: Boolean
        get() = hasDiabetes || hasHypertension || hasHeartDisease ||
                hasHepatitis || hasHiv || hasAsthma || hasBleedingDisorder ||
                isPregnant || isBreastfeeding || isSmoker ||
                allergies.isNotBlank() || currentMedications.isNotBlank()
}
