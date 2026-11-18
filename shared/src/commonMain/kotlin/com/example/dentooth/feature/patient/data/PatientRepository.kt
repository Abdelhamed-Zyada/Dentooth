package com.example.dentooth.feature.patient.data

import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.patient.domain.model.MedicalHistory
import com.example.dentooth.feature.patient.domain.model.Patient
import com.example.dentooth.feature.patient.domain.model.PatientGroup
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    fun getPatients(): Flow<List<Patient>>
    suspend fun getPatientById(id: String): Result<Patient>
    suspend fun savePatient(patient: Patient): Result<Patient>
    suspend fun deletePatient(id: String): Result<Unit>
    suspend fun getMedicalHistory(patientId: String): Result<MedicalHistory>
    suspend fun saveMedicalHistory(history: MedicalHistory): Result<MedicalHistory>
    suspend fun getPatientGroups(): Result<List<PatientGroup>>
}
