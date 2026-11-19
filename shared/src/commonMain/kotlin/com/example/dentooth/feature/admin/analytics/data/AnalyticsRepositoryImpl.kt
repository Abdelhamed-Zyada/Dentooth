package com.example.dentooth.feature.admin.analytics.data

import com.example.dentooth.feature.admin.analytics.domain.model.AcquisitionChannel
import com.example.dentooth.feature.admin.analytics.domain.model.ClinicAnalytics
import com.example.dentooth.feature.admin.analytics.domain.model.MonthlyRevenuePoint
import com.example.dentooth.feature.admin.analytics.domain.model.ProcedureShare
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnalyticsRepositoryImpl : AnalyticsRepository {

    private val sampleAnalytics = ClinicAnalytics(
        totalYearRevenue = 1485000.0,
        totalYearExpenses = 492000.0,
        netYearProfit = 993000.0,
        averageTicketPerPatient = 3250.0,
        chairUtilizationRate = 86,
        patientRetentionRate = 92,
        monthlyTrend = listOf(
            MonthlyRevenuePoint("Jun", 115000.0, 38000.0, 77000.0),
            MonthlyRevenuePoint("Jul", 128000.0, 42000.0, 86000.0),
            MonthlyRevenuePoint("Aug", 134000.0, 45000.0, 89000.0),
            MonthlyRevenuePoint("Sep", 142000.0, 46000.0, 96000.0),
            MonthlyRevenuePoint("Oct", 158000.0, 51000.0, 107000.0),
            MonthlyRevenuePoint("Nov", 168000.0, 53000.0, 115000.0)
        ),
        procedureShares = listOf(
            ProcedureShare("Implantology & Surgery", 48, 480000.0, 32, 0xFF00796B),
            ProcedureShare("Orthodontics", 36, 360000.0, 24, 0xFF7B1FA2),
            ProcedureShare("Endodontics (RCT)", 85, 290000.0, 20, 0xFF1976D2),
            ProcedureShare("Restorative & Crowns", 92, 235000.0, 16, 0xFFE65100),
            ProcedureShare("Preventive & Whitening", 110, 120000.0, 8, 0xFF388E3C)
        ),
        acquisitionChannels = listOf(
            AcquisitionChannel("Doctor & Patient Referrals", 142, 45, 0xFF1976D2),
            AcquisitionChannel("Google Search & Maps", 88, 28, 0xFF00796B),
            AcquisitionChannel("Social Media (Instagram/TikTok)", 54, 17, 0xFF7B1FA2),
            AcquisitionChannel("Direct Walk-in", 32, 10, 0xFFE65100)
        )
    )

    override fun getClinicAnalytics(): Flow<ClinicAnalytics> = MutableStateFlow(sampleAnalytics).asStateFlow()
}
