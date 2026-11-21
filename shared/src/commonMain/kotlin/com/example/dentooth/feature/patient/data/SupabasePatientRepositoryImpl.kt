package com.example.dentooth.feature.patient.data

import com.example.dentooth.core.network.SupabaseProvider
import com.example.dentooth.core.network.dto.PatientSupabaseDto
import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.patient.domain.model.MedicalHistory
import com.example.dentooth.feature.patient.domain.model.Patient
import com.example.dentooth.feature.patient.domain.model.PatientGroup
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SupabasePatientRepositoryImpl(
    private val fallback: PatientRepositoryImpl = PatientRepositoryImpl()
) : PatientRepository {

    private val supabase = SupabaseProvider.client
    private val _livePatients = MutableStateFlow<List<Patient>>(emptyList())
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        refreshPatients()
    }

    private fun refreshPatients() {
        scope.launch {
            try {
                val dtos = supabase.from("patients")
                    .select()
                    .decodeList<PatientSupabaseDto>()
                if (dtos.isNotEmpty()) {
                    _livePatients.value = dtos.map { it.toDomain() }
                }
            } catch (e: Exception) {
                // Table not created yet or network offline: fallback retains local data
            }
        }
    }

    override fun getPatients(): Flow<List<Patient>> {
        refreshPatients()
        return if (_livePatients.value.isNotEmpty()) {
            _livePatients.asStateFlow()
        } else {
            fallback.getPatients()
        }
    }

    override suspend fun getPatientById(id: String): Result<Patient> {
        return try {
            val dto = supabase.from("patients")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<PatientSupabaseDto>()
            if (dto != null) {
                Result.Success(dto.toDomain())
            } else {
                fallback.getPatientById(id)
            }
        } catch (e: Exception) {
            fallback.getPatientById(id)
        }
    }

    override suspend fun savePatient(patient: Patient): Result<Patient> {
        val dto = PatientSupabaseDto.fromDomain(patient)
        return try {
            supabase.from("patients").upsert(dto)
            refreshPatients()
            fallback.savePatient(patient)
        } catch (e: Exception) {
            fallback.savePatient(patient)
        }
    }

    override suspend fun deletePatient(id: String): Result<Unit> {
        return try {
            supabase.from("patients").delete {
                filter {
                    eq("id", id)
                }
            }
            refreshPatients()
            fallback.deletePatient(id)
        } catch (e: Exception) {
            fallback.deletePatient(id)
        }
    }

    override suspend fun getMedicalHistory(patientId: String): Result<MedicalHistory> {
        return fallback.getMedicalHistory(patientId)
    }

    override suspend fun saveMedicalHistory(history: MedicalHistory): Result<MedicalHistory> {
        return fallback.saveMedicalHistory(history)
    }

    override suspend fun getPatientGroups(): Result<List<PatientGroup>> {
        return fallback.getPatientGroups()
    }

    override suspend fun savePatientGroup(group: PatientGroup): Result<PatientGroup> {
        return fallback.savePatientGroup(group)
    }

    override suspend fun deletePatientGroup(id: String): Result<Unit> {
        return fallback.deletePatientGroup(id)
    }
}
