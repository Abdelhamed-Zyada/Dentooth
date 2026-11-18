package com.example.dentooth.core.auth.domain

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    ADMIN,
    DOCTOR,
    RECEPTIONIST,
    ACCOUNTANT,
    ASSISTANT;

    val displayName: String get() = when (this) {
        ADMIN -> "Clinic Administrator"
        DOCTOR -> "Dentist / Specialist"
        RECEPTIONIST -> "Receptionist"
        ACCOUNTANT -> "Accountant"
        ASSISTANT -> "Dental Assistant"
    }
}

@Serializable
data class UserSession(
    val id: String,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val clinicId: String,
    val branchId: String? = null,
    val avatarUrl: String? = null
)
