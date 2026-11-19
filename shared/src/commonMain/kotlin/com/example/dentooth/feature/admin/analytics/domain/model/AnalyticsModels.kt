package com.example.dentooth.feature.admin.analytics.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class MonthlyRevenuePoint(
    val monthName: String,
    val revenue: Double,
    val expenses: Double,
    val netProfit: Double
)

@Serializable
data class ProcedureShare(
    val categoryName: String,
    val procedureCount: Int,
    val revenueEgp: Double,
    val percentage: Int,
    val colorHex: Long
) {
    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
data class AcquisitionChannel(
    val channelName: String,
    val patientCount: Int,
    val percentage: Int,
    val colorHex: Long
) {
    val composeColor: Color
        get() = Color(colorHex)
}

@Serializable
data class ClinicAnalytics(
    val totalYearRevenue: Double,
    val totalYearExpenses: Double,
    val netYearProfit: Double,
    val averageTicketPerPatient: Double,
    val chairUtilizationRate: Int, // e.g. 84%
    val patientRetentionRate: Int, // e.g. 91%
    val monthlyTrend: List<MonthlyRevenuePoint>,
    val procedureShares: List<ProcedureShare>,
    val acquisitionChannels: List<AcquisitionChannel>
)
