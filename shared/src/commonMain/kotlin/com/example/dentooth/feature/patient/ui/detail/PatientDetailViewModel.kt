package com.example.dentooth.feature.patient.ui.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.patient.data.PatientRepository
import com.example.dentooth.feature.patient.domain.model.MedicalHistory
import com.example.dentooth.feature.patient.domain.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PatientDetailUiState(
    val patientState: UiState<Patient> = UiState.Loading,
    val medicalHistoryState: UiState<MedicalHistory> = UiState.Loading,
    val selectedTab: Int = 0, // 0: Overview, 1: Medical History, 2: Notes
    val isSavingMedicalHistory: Boolean = false
)

class PatientDetailViewModel(
    private val patientId: String,
    private val patientRepository: PatientRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(PatientDetailUiState())
    val uiState: StateFlow<PatientDetailUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(patientState = UiState.Loading)
            val patientResult = patientRepository.getPatientById(patientId)
            patientResult.onSuccess { patient ->
                _uiState.value = _uiState.value.copy(patientState = UiState.Success(patient))
            }.onError { msg, _ ->
                _uiState.value = _uiState.value.copy(patientState = UiState.Error(msg))
            }

            val medResult = patientRepository.getMedicalHistory(patientId)
            medResult.onSuccess { med ->
                _uiState.value = _uiState.value.copy(medicalHistoryState = UiState.Success(med))
            }.onError { msg, _ ->
                _uiState.value = _uiState.value.copy(medicalHistoryState = UiState.Error(msg))
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun updateMedicalHistory(history: MedicalHistory) {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingMedicalHistory = true)
            val result = patientRepository.saveMedicalHistory(history)
            result.onSuccess { saved ->
                _uiState.value = _uiState.value.copy(
                    medicalHistoryState = UiState.Success(saved),
                    isSavingMedicalHistory = false
                )
            }.onError { _, _ ->
                _uiState.value = _uiState.value.copy(isSavingMedicalHistory = false)
            }
        }
    }

    fun updatePatientNotes(notes: String) {
        val currentPatient = _uiState.value.patientState.dataOrNull() ?: return
        screenModelScope.launch {
            val updated = currentPatient.copy(notes = notes.trim())
            patientRepository.savePatient(updated)
            _uiState.value = _uiState.value.copy(patientState = UiState.Success(updated))
        }
    }

    fun deletePatient(onComplete: () -> Unit) {
        screenModelScope.launch {
            patientRepository.deletePatient(patientId)
            onComplete()
        }
    }
}
