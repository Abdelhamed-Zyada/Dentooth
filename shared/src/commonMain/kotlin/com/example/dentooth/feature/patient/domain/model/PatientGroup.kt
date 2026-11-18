package com.example.dentooth.feature.patient.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PatientGroup(
    val id: String,
    val name: String,
    val colorHex: String = "#1B6B93",
    val description: String? = null
)
