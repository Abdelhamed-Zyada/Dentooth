package com.example.dentooth.feature.treatment.data

import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.treatment.domain.model.PlanProcedure
import com.example.dentooth.feature.treatment.domain.model.PlanStatus
import com.example.dentooth.feature.treatment.domain.model.ProcedureCatalogItem
import com.example.dentooth.feature.treatment.domain.model.ProcedureStatus
import com.example.dentooth.feature.treatment.domain.model.TreatmentPlan
import kotlinx.coroutines.flow.Flow

interface TreatmentRepository {
    fun getTreatmentPlans(patientId: String): Flow<List<TreatmentPlan>>
    suspend fun getTreatmentPlanById(planId: String): Result<TreatmentPlan>
    suspend fun saveTreatmentPlan(plan: TreatmentPlan): Result<TreatmentPlan>
    suspend fun updatePlanStatus(planId: String, status: PlanStatus): Result<TreatmentPlan>
    suspend fun addProcedureToPlan(planId: String, procedure: PlanProcedure): Result<TreatmentPlan>
    suspend fun updateProcedureStatus(planId: String, procedureId: String, status: ProcedureStatus): Result<TreatmentPlan>
    suspend fun removeProcedureFromPlan(planId: String, procedureId: String): Result<TreatmentPlan>
    suspend fun getProcedureCatalog(): Result<List<ProcedureCatalogItem>>
}
