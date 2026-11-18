package com.example.dentooth.feature.dentalchart.data

import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.dentalchart.domain.model.DentalChart
import com.example.dentooth.feature.dentalchart.domain.model.ToothCondition
import com.example.dentooth.feature.dentalchart.domain.model.ToothRecord
import com.example.dentooth.feature.dentalchart.domain.model.ToothStatus
import com.example.dentooth.feature.dentalchart.domain.model.ToothSurface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class DentalChartRepositoryImpl : DentalChartRepository {

    // Store in-memory charts per patient
    private val chartsMap = mutableMapOf<String, MutableStateFlow<DentalChart>>()

    private fun getOrCreateChartFlow(patientId: String): MutableStateFlow<DentalChart> {
        return chartsMap.getOrPut(patientId) {
            val initialChart = if (patientId == "pat-001") {
                // Seed realistic dental chart for Ahmed Hassan
                val seedTeeth = mutableMapOf<Int, ToothRecord>()

                // Tooth 16: Caries on Occlusal surface
                seedTeeth[16] = ToothRecord(
                    toothNumber = 16,
                    status = ToothStatus.CARIES,
                    affectedSurfaces = setOf(ToothSurface.OCCLUSAL),
                    surfaceStatuses = mapOf(ToothSurface.OCCLUSAL to ToothStatus.CARIES),
                    conditions = listOf(
                        ToothCondition(
                            id = "c-1",
                            toothNumber = 16,
                            type = "Deep Dentinal Caries",
                            surfaces = listOf(ToothSurface.OCCLUSAL),
                            severity = "Severe",
                            diagnosedAt = "2026-11-10"
                        )
                    ),
                    notes = "Pain on thermal stimuli. Pulp sensitivity positive."
                )

                // Tooth 26: Root canal treated
                seedTeeth[26] = ToothRecord(
                    toothNumber = 26,
                    status = ToothStatus.ROOT_CANAL,
                    notes = "Obturation complete. Post and core recommended."
                )

                // Tooth 24: Composite filling on Mesial-Occlusal
                seedTeeth[24] = ToothRecord(
                    toothNumber = 24,
                    status = ToothStatus.FILLED,
                    affectedSurfaces = setOf(ToothSurface.MESIAL, ToothSurface.OCCLUSAL),
                    surfaceStatuses = mapOf(
                        ToothSurface.MESIAL to ToothStatus.FILLED,
                        ToothSurface.OCCLUSAL to ToothStatus.FILLED
                    ),
                    notes = "Class II composite restoration sound."
                )

                // Tooth 36: Missing (extracted)
                seedTeeth[36] = ToothRecord(
                    toothNumber = 36,
                    status = ToothStatus.MISSING,
                    notes = "Extracted 3 years ago. Candidate for dental implant."
                )

                // Tooth 46: Zirconia Crown
                seedTeeth[46] = ToothRecord(
                    toothNumber = 46,
                    status = ToothStatus.CROWN,
                    notes = "Monolithic zirconia crown cemented."
                )

                DentalChart(
                    id = "chart-$patientId",
                    patientId = patientId,
                    isPediatric = false,
                    teeth = seedTeeth
                )
            } else if (patientId == "pat-004") {
                // Pediatric patient (Laila)
                val seedTeeth = mutableMapOf<Int, ToothRecord>()
                seedTeeth[55] = ToothRecord(toothNumber = 55, status = ToothStatus.FILLED, isPrimary = true)
                seedTeeth[65] = ToothRecord(toothNumber = 65, status = ToothStatus.FILLED, isPrimary = true)

                DentalChart(
                    id = "chart-$patientId",
                    patientId = patientId,
                    isPediatric = true,
                    teeth = seedTeeth
                )
            } else {
                DentalChart(
                    id = "chart-$patientId",
                    patientId = patientId,
                    isPediatric = false,
                    teeth = emptyMap()
                )
            }
            MutableStateFlow(initialChart)
        }
    }

    override fun getDentalChart(patientId: String): Flow<DentalChart> {
        return getOrCreateChartFlow(patientId)
    }

    override suspend fun updateToothStatus(
        patientId: String,
        toothNumber: Int,
        status: ToothStatus
    ): Result<ToothRecord> {
        val flow = getOrCreateChartFlow(patientId)
        val current = flow.value
        val existing = current.getTooth(toothNumber)
        val updated = existing.copy(
            status = status,
            surfaceStatuses = if (status == ToothStatus.HEALTHY) emptyMap() else existing.surfaceStatuses
        )
        val newTeeth = current.teeth.toMutableMap()
        newTeeth[toothNumber] = updated
        flow.value = current.copy(teeth = newTeeth)
        return Result.Success(updated)
    }

    override suspend fun updateSurfaceStatus(
        patientId: String,
        toothNumber: Int,
        surface: ToothSurface,
        status: ToothStatus
    ): Result<ToothRecord> {
        val flow = getOrCreateChartFlow(patientId)
        val current = flow.value
        val existing = current.getTooth(toothNumber)

        val newSurfaceStatuses = existing.surfaceStatuses.toMutableMap()
        if (status == ToothStatus.HEALTHY) {
            newSurfaceStatuses.remove(surface)
        } else {
            newSurfaceStatuses[surface] = status
        }

        val updated = existing.copy(
            surfaceStatuses = newSurfaceStatuses,
            status = if (newSurfaceStatuses.isNotEmpty()) newSurfaceStatuses.values.first() else ToothStatus.HEALTHY
        )
        val newTeeth = current.teeth.toMutableMap()
        newTeeth[toothNumber] = updated
        flow.value = current.copy(teeth = newTeeth)
        return Result.Success(updated)
    }

    override suspend fun addToothCondition(
        patientId: String,
        toothNumber: Int,
        condition: ToothCondition
    ): Result<ToothRecord> {
        val flow = getOrCreateChartFlow(patientId)
        val current = flow.value
        val existing = current.getTooth(toothNumber)
        val updatedConditions = existing.conditions + condition
        val updated = existing.copy(conditions = updatedConditions)
        val newTeeth = current.teeth.toMutableMap()
        newTeeth[toothNumber] = updated
        flow.value = current.copy(teeth = newTeeth)
        return Result.Success(updated)
    }

    override suspend fun removeToothCondition(
        patientId: String,
        toothNumber: Int,
        conditionId: String
    ): Result<ToothRecord> {
        val flow = getOrCreateChartFlow(patientId)
        val current = flow.value
        val existing = current.getTooth(toothNumber)
        val updatedConditions = existing.conditions.filter { it.id != conditionId }
        val updated = existing.copy(conditions = updatedConditions)
        val newTeeth = current.teeth.toMutableMap()
        newTeeth[toothNumber] = updated
        flow.value = current.copy(teeth = newTeeth)
        return Result.Success(updated)
    }

    override suspend fun updateToothNotes(
        patientId: String,
        toothNumber: Int,
        notes: String
    ): Result<ToothRecord> {
        val flow = getOrCreateChartFlow(patientId)
        val current = flow.value
        val existing = current.getTooth(toothNumber)
        val updated = existing.copy(notes = notes)
        val newTeeth = current.teeth.toMutableMap()
        newTeeth[toothNumber] = updated
        flow.value = current.copy(teeth = newTeeth)
        return Result.Success(updated)
    }

    override suspend fun togglePediatricMode(
        patientId: String,
        isPediatric: Boolean
    ): Result<DentalChart> {
        val flow = getOrCreateChartFlow(patientId)
        val updated = flow.value.copy(isPediatric = isPediatric)
        flow.value = updated
        return Result.Success(updated)
    }
}
