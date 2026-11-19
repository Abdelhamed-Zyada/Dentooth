package com.example.dentooth.feature.admin.settings.data

import com.example.dentooth.feature.admin.settings.domain.model.ClinicSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl : SettingsRepository {

    private val _settings = MutableStateFlow(ClinicSettings())

    override fun getSettings(): StateFlow<ClinicSettings> = _settings.asStateFlow()

    override suspend fun updateSettings(settings: ClinicSettings): Result<ClinicSettings> {
        _settings.value = settings
        return Result.success(settings)
    }
}
