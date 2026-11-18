package com.example.dentooth.feature.treatment.data

import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.treatment.domain.model.PlanProcedure
import com.example.dentooth.feature.treatment.domain.model.PlanStatus
import com.example.dentooth.feature.treatment.domain.model.ProcedureCategory
import com.example.dentooth.feature.treatment.domain.model.ProcedureCatalogItem
import com.example.dentooth.feature.treatment.domain.model.ProcedureStatus
import com.example.dentooth.feature.treatment.domain.model.TreatmentPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TreatmentRepositoryImpl : TreatmentRepository {

    private val seedCatalog = listOf(
        ProcedureCatalogItem(
            id = "cat-1",
            code = "D0120",
            name = "Periodic Comprehensive Oral Exam",
            nameAr = "كشف فم وأسنان شامل",
            category = ProcedureCategory.DIAGNOSTIC,
            defaultCost = 250.0,
            estimatedDurationMinutes = 20
        ),
        ProcedureCatalogItem(
            id = "cat-2",
            code = "D0330",
            name = "Panoramic Digital Radiograph (OPG)",
            nameAr = "أشعة بانوراما رقمية",
            category = ProcedureCategory.DIAGNOSTIC,
            defaultCost = 350.0,
            estimatedDurationMinutes = 15
        ),
        ProcedureCatalogItem(
            id = "cat-3",
            code = "D1110",
            name = "Ultrasonic Dental Scaling & Polishing",
            nameAr = "تنظيف وتلميع الأسنان وإزالة الجير",
            category = ProcedureCategory.PREVENTIVE,
            defaultCost = 700.0,
            estimatedDurationMinutes = 40
        ),
        ProcedureCatalogItem(
            id = "cat-4",
            code = "D2391",
            name = "Composite Restoration (1 Surface)",
            nameAr = "حشو تجميلي كومبوزيت (سطح واحد)",
            category = ProcedureCategory.RESTORATIVE,
            defaultCost = 850.0,
            estimatedDurationMinutes = 30
        ),
        ProcedureCatalogItem(
            id = "cat-5",
            code = "D2392",
            name = "Composite Restoration (2 Surfaces)",
            nameAr = "حشو تجميلي كومبوزيت (سطحين)",
            category = ProcedureCategory.RESTORATIVE,
            defaultCost = 1200.0,
            estimatedDurationMinutes = 45
        ),
        ProcedureCatalogItem(
            id = "cat-6",
            code = "D3330",
            name = "Endodontic Therapy - Molar Tooth",
            nameAr = "علاج جذور وعصب ضرس خلفي",
            category = ProcedureCategory.ENDODONTIC,
            defaultCost = 2800.0,
            estimatedDurationMinutes = 60
        ),
        ProcedureCatalogItem(
            id = "cat-7",
            code = "D2950",
            name = "Fiber Post and Composite Core Build-up",
            nameAr = "بناء الضرس بدعامة فايبر",
            category = ProcedureCategory.ENDODONTIC,
            defaultCost = 950.0,
            estimatedDurationMinutes = 30
        ),
        ProcedureCatalogItem(
            id = "cat-8",
            code = "D2740",
            name = "Monolithic Zirconia Crown",
            nameAr = "تاج زركونيا كامل",
            category = ProcedureCategory.PROSTHODONTIC,
            defaultCost = 3800.0,
            estimatedDurationMinutes = 45
        ),
        ProcedureCatalogItem(
            id = "cat-9",
            code = "D2962",
            name = "E-Max Porcelain Veneer",
            nameAr = "فينير تجميلي إيماكس",
            category = ProcedureCategory.PROSTHODONTIC,
            defaultCost = 4500.0,
            estimatedDurationMinutes = 45
        ),
        ProcedureCatalogItem(
            id = "cat-10",
            code = "D7140",
            name = "Routine Tooth Extraction",
            nameAr = "خلع سن عادي",
            category = ProcedureCategory.SURGICAL,
            defaultCost = 500.0,
            estimatedDurationMinutes = 30
        ),
        ProcedureCatalogItem(
            id = "cat-11",
            code = "D7210",
            name = "Surgical Impacted Wisdom Tooth Extraction",
            nameAr = "خلع جراحي لضرس العقل المدفون",
            category = ProcedureCategory.SURGICAL,
            defaultCost = 2000.0,
            estimatedDurationMinutes = 60
        ),
        ProcedureCatalogItem(
            id = "cat-12",
            code = "D6010",
            name = "Surgical Dental Implant Placement (Titanium)",
            nameAr = "زراعة سن تيتانيوم",
            category = ProcedureCategory.SURGICAL,
            defaultCost = 12500.0,
            estimatedDurationMinutes = 60
        )
    )

    private val seedPlans = mutableListOf(
        TreatmentPlan(
            id = "tp-001",
            patientId = "pat-001",
            doctorName = "Dr. Ahmed Hassan",
            title = "Endo & Restoration Phase 1",
            status = PlanStatus.ACTIVE,
            procedures = listOf(
                PlanProcedure(
                    id = "pr-1",
                    planId = "tp-001",
                    toothNumber = 16,
                    procedureName = "Endodontic Therapy - Molar Tooth",
                    category = ProcedureCategory.ENDODONTIC,
                    cost = 2800.0,
                    status = ProcedureStatus.IN_PROGRESS,
                    notes = "Working length determined. Canal instrumentation underway."
                ),
                PlanProcedure(
                    id = "pr-2",
                    planId = "tp-001",
                    toothNumber = 16,
                    procedureName = "Monolithic Zirconia Crown",
                    category = ProcedureCategory.PROSTHODONTIC,
                    cost = 3800.0,
                    status = ProcedureStatus.PLANNED,
                    notes = "Scheduled after endodontic obturation."
                ),
                PlanProcedure(
                    id = "pr-3",
                    planId = "tp-001",
                    toothNumber = 24,
                    procedureName = "Composite Restoration (2 Surfaces)",
                    category = ProcedureCategory.RESTORATIVE,
                    cost = 1200.0,
                    status = ProcedureStatus.COMPLETED,
                    completedAt = "2026-11-10",
                    notes = "MO cavity cleaned and restored."
                )
            ),
            discount = 300.0,
            notes = "Full treatment sequence agreed with patient.",
            createdAt = "2026-11-05"
        ),
        TreatmentPlan(
            id = "tp-002",
            patientId = "pat-001",
            doctorName = "Dr. Ahmed Hassan",
            title = "Implant Rehabilitation (#36)",
            status = PlanStatus.PLANNED,
            procedures = listOf(
                PlanProcedure(
                    id = "pr-4",
                    planId = "tp-002",
                    toothNumber = 36,
                    procedureName = "Surgical Dental Implant Placement (Titanium)",
                    category = ProcedureCategory.SURGICAL,
                    cost = 12500.0,
                    status = ProcedureStatus.PLANNED
                )
            ),
            createdAt = "2026-11-12"
        )
    )

    private val plansFlow = MutableStateFlow(seedPlans)

    override fun getTreatmentPlans(patientId: String): Flow<List<TreatmentPlan>> {
        val filtered = MutableStateFlow(plansFlow.value.filter { it.patientId == patientId })
        return filtered.asStateFlow()
    }

    override suspend fun getTreatmentPlanById(planId: String): Result<TreatmentPlan> {
        val plan = plansFlow.value.find { it.id == planId }
        return if (plan != null) Result.Success(plan) else Result.Error("Plan not found: $planId")
    }

    override suspend fun saveTreatmentPlan(plan: TreatmentPlan): Result<TreatmentPlan> {
        val current = plansFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == plan.id }
        val saved = if (index >= 0) {
            current[index] = plan
            plan
        } else {
            val newPlan = plan.copy(
                id = if (plan.id.isBlank()) "tp-${plansFlow.value.size + 1}" else plan.id
            )
            current.add(0, newPlan)
            newPlan
        }
        plansFlow.value = current
        return Result.Success(saved)
    }

    override suspend fun updatePlanStatus(planId: String, status: PlanStatus): Result<TreatmentPlan> {
        val current = plansFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == planId }
        if (index >= 0) {
            val updated = current[index].copy(status = status)
            current[index] = updated
            plansFlow.value = current
            return Result.Success(updated)
        }
        return Result.Error("Plan not found: $planId")
    }

    override suspend fun addProcedureToPlan(
        planId: String,
        procedure: PlanProcedure
    ): Result<TreatmentPlan> {
        val current = plansFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == planId }
        if (index >= 0) {
            val plan = current[index]
            val updatedProcedures = plan.procedures + procedure.copy(
                id = if (procedure.id.isBlank()) "pr-${plan.procedures.size + 10}" else procedure.id,
                planId = planId
            )
            val updated = plan.copy(procedures = updatedProcedures)
            current[index] = updated
            plansFlow.value = current
            return Result.Success(updated)
        }
        return Result.Error("Plan not found: $planId")
    }

    override suspend fun updateProcedureStatus(
        planId: String,
        procedureId: String,
        status: ProcedureStatus
    ): Result<TreatmentPlan> {
        val current = plansFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == planId }
        if (index >= 0) {
            val plan = current[index]
            val updatedProcedures = plan.procedures.map { proc ->
                if (proc.id == procedureId) proc.copy(status = status) else proc
            }
            val updated = plan.copy(procedures = updatedProcedures)
            current[index] = updated
            plansFlow.value = current
            return Result.Success(updated)
        }
        return Result.Error("Plan not found: $planId")
    }

    override suspend fun removeProcedureFromPlan(
        planId: String,
        procedureId: String
    ): Result<TreatmentPlan> {
        val current = plansFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == planId }
        if (index >= 0) {
            val plan = current[index]
            val updatedProcedures = plan.procedures.filter { it.id != procedureId }
            val updated = plan.copy(procedures = updatedProcedures)
            current[index] = updated
            plansFlow.value = current
            return Result.Success(updated)
        }
        return Result.Error("Plan not found: $planId")
    }

    override suspend fun getProcedureCatalog(): Result<List<ProcedureCatalogItem>> {
        return Result.Success(seedCatalog)
    }
}
