package com.example.dentooth.feature.patient.ui.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.patient.data.PatientRepository
import com.example.dentooth.feature.patient.domain.model.Patient
import com.example.dentooth.feature.patient.domain.model.PatientGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PatientListUiState(
    val patients: List<Patient> = emptyList(),
    val filteredPatients: List<Patient> = emptyList(),
    val searchQuery: String = "",
    val selectedGroup: String? = null,
    val availableGroups: List<PatientGroup> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PatientListViewModel(
    private val patientRepository: PatientRepository
) : ScreenModel {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedGroup = MutableStateFlow<String?>(null)
    private val _availableGroups = MutableStateFlow<List<PatientGroup>>(emptyList())
    private val _errorMessage = MutableStateFlow<String?>(null)

    init {
        loadGroups()
    }

    private fun loadGroups() {
        screenModelScope.launch {
            patientRepository.getPatientGroups().onSuccess { groups ->
                _availableGroups.value = groups
            }
        }
    }

    val state: StateFlow<PatientListUiState> = combine(
        patientRepository.getPatients(),
        _searchQuery,
        _selectedGroup,
        _availableGroups,
        _errorMessage
    ) { allPatients, query, groupFilter, groups, err ->
        val filtered = allPatients.filter { patient ->
            val matchesQuery = query.isBlank() ||
                    patient.fullName.contains(query, ignoreCase = true) ||
                    patient.phone.contains(query) ||
                    patient.patientNumber.contains(query, ignoreCase = true)

            val matchesGroup = groupFilter == null || patient.groups.contains(groupFilter)

            matchesQuery && matchesGroup
        }

        PatientListUiState(
            patients = allPatients,
            filteredPatients = filtered,
            searchQuery = query,
            selectedGroup = groupFilter,
            availableGroups = groups,
            isLoading = false,
            errorMessage = err
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PatientListUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onGroupSelected(groupName: String?) {
        _selectedGroup.value = if (_selectedGroup.value == groupName) null else groupName
    }

    fun deletePatient(patientId: String) {
        screenModelScope.launch {
            patientRepository.deletePatient(patientId)
        }
    }
}
