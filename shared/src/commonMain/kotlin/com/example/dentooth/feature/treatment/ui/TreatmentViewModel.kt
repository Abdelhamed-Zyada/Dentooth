package com.example.dentooth.feature.treatment.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.feature.treatment.data.TreatmentRepository
import com.example.dentooth.feature.treatment.domain.model.PlanProcedure
import com.example.dentooth.feature.treatment.domain.model.PlanStatus
import com.example.dentooth.feature.treatment.domain.model.ProcedureCatalogItem
import com.example.dentooth.feature.treatment.domain.model.ProcedureCategory
import com.example.dentooth.feature.treatment.domain.model.ProcedureStatus
import com.example.dentooth.feature.treatment.domain.model.TreatmentPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TreatmentUiState(
    val plans: List<TreatmentPlan> = emptyList(),
    val catalog: List<ProcedureCatalogItem> = emptyList(),
    val isLoading: Boolean = false,
    val isCreatingPlan: Boolean = false
)

class TreatmentViewModel(
    private val patientId: String,
    private val treatmentRepository: TreatmentRepository
) : ScreenModel {

    private val _catalog = MutableStateFlow<List<ProcedureCatalogItem>>(emptyList())
    private val _isCreatingPlan = MutableStateFlow(false)

    init {
        loadCatalog()
    }

    private fun loadCatalog() {
        screenModelScope.launch {
            treatmentRepository.getProcedureCatalog().onSuccess {
                _catalog.value = it
            }
        }
    }

    val state: StateFlow<TreatmentUiState> = combine(
        treatmentRepository.getTreatmentPlans(patientId),
        _catalog,
        _isCreatingPlan
    ) { plans, catalog, creating ->
        TreatmentUiState(
            plans = plans,
            catalog = catalog,
            isLoading = false,
            isCreatingPlan = creating
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TreatmentUiState(isLoading = true)
    )

    fun createPlan(title: String) {
        if (title.isBlank()) return
        screenModelScope.launch {
            val newPlan = TreatmentPlan(
                id = "",
                patientId = patientId,
                title = title.trim(),
                status = PlanStatus.PLANNED,
                createdAt = "Today"
            )
            treatmentRepository.saveTreatmentPlan(newPlan)
        }
    }

    fun addProcedure(
        planId: String,
        catalogItem: ProcedureCatalogItem,
        toothNumber: Int?,
        customCost: Double?
    ) {
        screenModelScope.launch {
            val procedure = PlanProcedure(
                id = "",
                planId = planId,
                toothNumber = toothNumber,
                procedureName = catalogItem.name,
                category = catalogItem.category,
                cost = customCost ?: catalogItem.defaultCost,
                status = ProcedureStatus.PLANNED
            )
            treatmentRepository.addProcedureToPlan(planId, procedure)
        }
    }

    fun updateProcedureStatus(planId: String, procedureId: String, status: ProcedureStatus) {
        screenModelScope.launch {
            treatmentRepository.updateProcedureStatus(planId, procedureId, status)
        }
    }

    fun updatePlanStatus(planId: String, status: PlanStatus) {
        screenModelScope.launch {
            treatmentRepository.updatePlanStatus(planId, status)
        }
    }
}
