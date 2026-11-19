package com.example.dentooth.feature.admin.branch.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Branch(
    val id: String,
    val name: String,
    val code: String,
    val address: String,
    val city: String = "Cairo",
    val phone: String,
    val chairCount: Int,
    val operatingHours: String = "09:00 AM - 10:00 PM",
    val isMainBranch: Boolean = false,
    val isActive: Boolean = true
)
