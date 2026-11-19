package com.example.dentooth.feature.admin.analytics.data

import com.example.dentooth.feature.admin.analytics.domain.model.ClinicAnalytics
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getClinicAnalytics(): Flow<ClinicAnalytics>
}
