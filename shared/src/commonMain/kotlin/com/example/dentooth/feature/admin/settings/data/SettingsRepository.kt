package com.example.dentooth.feature.admin.settings.data

import com.example.dentooth.feature.admin.settings.domain.model.ClinicSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    fun getSettings(): StateFlow<ClinicSettings>
    suspend fun updateSettings(settings: ClinicSettings): Result<ClinicSettings>
}
