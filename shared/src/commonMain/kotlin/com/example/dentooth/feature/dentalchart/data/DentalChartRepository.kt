package com.example.dentooth.feature.dentalchart.data

import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.dentalchart.domain.model.DentalChart
import com.example.dentooth.feature.dentalchart.domain.model.ToothCondition
import com.example.dentooth.feature.dentalchart.domain.model.ToothRecord
import com.example.dentooth.feature.dentalchart.domain.model.ToothStatus
import com.example.dentooth.feature.dentalchart.domain.model.ToothSurface
import kotlinx.coroutines.flow.Flow

interface DentalChartRepository {
    fun getDentalChart(patientId: String): Flow<DentalChart>
    suspend fun updateToothStatus(patientId: String, toothNumber: Int, status: ToothStatus): Result<ToothRecord>
    suspend fun updateSurfaceStatus(patientId: String, toothNumber: Int, surface: ToothSurface, status: ToothStatus): Result<ToothRecord>
    suspend fun addToothCondition(patientId: String, toothNumber: Int, condition: ToothCondition): Result<ToothRecord>
    suspend fun removeToothCondition(patientId: String, toothNumber: Int, conditionId: String): Result<ToothRecord>
    suspend fun updateToothNotes(patientId: String, toothNumber: Int, notes: String): Result<ToothRecord>
    suspend fun togglePediatricMode(patientId: String, isPediatric: Boolean): Result<DentalChart>
}
