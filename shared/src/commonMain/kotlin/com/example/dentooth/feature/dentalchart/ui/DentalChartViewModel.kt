package com.example.dentooth.feature.dentalchart.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.dentalchart.data.DentalChartRepository
import com.example.dentooth.feature.dentalchart.domain.model.DentalChart
import com.example.dentooth.feature.dentalchart.domain.model.ToothCondition
import com.example.dentooth.feature.dentalchart.domain.model.ToothRecord
import com.example.dentooth.feature.dentalchart.domain.model.ToothStatus
import com.example.dentooth.feature.dentalchart.domain.model.ToothSurface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DentalChartUiState(
    val chart: DentalChart? = null,
    val selectedTooth: ToothRecord? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DentalChartViewModel(
    private val patientId: String,
    private val dentalChartRepository: DentalChartRepository
) : ScreenModel {

    private val _selectedTooth = MutableStateFlow<ToothRecord?>(null)
    val selectedTooth: StateFlow<ToothRecord?> = _selectedTooth.asStateFlow()

    val state: StateFlow<DentalChartUiState> = dentalChartRepository.getDentalChart(patientId)
        .map { chart ->
            val curSelected = _selectedTooth.value
            val refreshedSelected = if (curSelected != null) chart.getTooth(curSelected.toothNumber) else null
            DentalChartUiState(
                chart = chart,
                selectedTooth = refreshedSelected,
                isLoading = false
            )
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DentalChartUiState(isLoading = true)
        )

    fun selectTooth(tooth: ToothRecord) {
        _selectedTooth.value = tooth
    }

    fun clearSelectedTooth() {
        _selectedTooth.value = null
    }

    fun updateToothStatus(status: ToothStatus) {
        val tooth = _selectedTooth.value ?: return
        screenModelScope.launch {
            dentalChartRepository.updateToothStatus(patientId, tooth.toothNumber, status)
                .onSuccess { updated ->
                    _selectedTooth.value = updated
                }
        }
    }

    fun updateSurfaceStatus(surface: ToothSurface, status: ToothStatus) {
        val tooth = _selectedTooth.value ?: return
        screenModelScope.launch {
            dentalChartRepository.updateSurfaceStatus(patientId, tooth.toothNumber, surface, status)
                .onSuccess { updated ->
                    _selectedTooth.value = updated
                }
        }
    }

    fun addToothCondition(condition: ToothCondition) {
        val tooth = _selectedTooth.value ?: return
        screenModelScope.launch {
            dentalChartRepository.addToothCondition(patientId, tooth.toothNumber, condition)
                .onSuccess { updated ->
                    _selectedTooth.value = updated
                }
        }
    }

    fun togglePediatricMode(isPediatric: Boolean) {
        screenModelScope.launch {
            dentalChartRepository.togglePediatricMode(patientId, isPediatric)
        }
    }
}
