package com.example.dentooth.feature.admin.analytics.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.admin.analytics.data.AnalyticsRepository
import com.example.dentooth.feature.admin.analytics.domain.model.ClinicAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val analytics: UiState<ClinicAnalytics> = UiState.Loading
)

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        screenModelScope.launch {
            analyticsRepository.getClinicAnalytics().collect { data ->
                _uiState.value = AnalyticsUiState(analytics = UiState.Success(data))
            }
        }
    }
}
