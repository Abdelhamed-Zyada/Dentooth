package com.example.dentooth.feature.patient.data

import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.core.util.Result
import com.example.dentooth.feature.patient.domain.model.Gender
import com.example.dentooth.feature.patient.domain.model.MedicalHistory
import com.example.dentooth.feature.patient.domain.model.Patient
import com.example.dentooth.feature.patient.domain.model.PatientGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class PatientRepositoryImpl : PatientRepository {

    private val seedPatients = listOf(
        Patient(
            id = "pat-001",
            clinicId = "cln-001",
            patientNumber = "P-00001",
            firstName = "Ahmed",
            lastName = "Hassan",
            phone = "+20 100 123 4567",
            email = "ahmed.hassan@example.com",
            dateOfBirth = LocalDate(1989, 4, 15),
            gender = Gender.MALE,
            address = "Nasr City, Cairo",
            bloodType = "A+",
            notes = "Patient complains of sharp pain in upper right molar (#16).",
            groups = listOf("VIP", "Endo"),
            totalBilled = 4500.0,
            totalPaid = 3000.0,
            lastVisitDate = LocalDate(2026, 11, 10)
        ),
        Patient(
            id = "pat-002",
            clinicId = "cln-001",
            patientNumber = "P-00002",
            firstName = "Sara",
            lastName = "Mohamed",
            phone = "+20 111 987 6543",
            email = "sara.m@example.com",
            dateOfBirth = LocalDate(1996, 8, 22),
            gender = Gender.FEMALE,
            address = "Maadi, Cairo",
            bloodType = "O+",
            notes = "Monthly ortho adjustment session.",
            groups = listOf("Orthodontics"),
            totalBilled = 18000.0,
            totalPaid = 12000.0,
            lastVisitDate = LocalDate(2026, 11, 15)
        ),
        Patient(
            id = "pat-003",
            clinicId = "cln-001",
            patientNumber = "P-00003",
            firstName = "Mahmoud",
            lastName = "Tarek",
            phone = "+20 102 345 6789",
            email = "m.tarek@example.com",
            dateOfBirth = LocalDate(1980, 1, 30),
            gender = Gender.MALE,
            address = "Zamalek, Cairo",
            bloodType = "B+",
            notes = "Zirconia crown preparation on #26 completed.",
            groups = listOf("Prostho"),
            totalBilled = 7500.0,
            totalPaid = 7500.0,
            lastVisitDate = LocalDate(2026, 11, 5)
        ),
        Patient(
            id = "pat-004",
            clinicId = "cln-001",
            patientNumber = "P-00004",
            firstName = "Laila",
            lastName = "Ibrahim",
            phone = "+20 122 555 1212",
            dateOfBirth = LocalDate(2017, 6, 10),
            gender = Gender.FEMALE,
            address = "New Cairo, 5th Settlement",
            bloodType = "A-",
            notes = "Pediatric check-up. Sealant placed on #55 and #65.",
            groups = listOf("Pediatric"),
            totalBilled = 1200.0,
            totalPaid = 1200.0,
            lastVisitDate = LocalDate(2026, 10, 28)
        ),
        Patient(
            id = "pat-005",
            clinicId = "cln-001",
            patientNumber = "P-00005",
            firstName = "Omar",
            lastName = "Khaled",
            phone = "+20 109 876 5432",
            dateOfBirth = LocalDate(1975, 12, 5),
            gender = Gender.MALE,
            address = "Dokki, Giza",
            bloodType = "AB+",
            notes = "Post-implant evaluation. Healing progressing well.",
            groups = listOf("Surgery", "VIP"),
            totalBilled = 25000.0,
            totalPaid = 20000.0,
            lastVisitDate = LocalDate(2026, 11, 12)
        )
    )

    private val seedMedicalHistories = mutableMapOf(
        "pat-001" to MedicalHistory(
            id = "med-001",
            patientId = "pat-001",
            hasDiabetes = true,
            hasHypertension = false,
            allergies = "Penicillin, Amoxicillin",
            currentMedications = "Metformin 500mg daily",
            lastUpdated = DateTimeUtil.currentInstant().toString()
        ),
        "pat-002" to MedicalHistory(
            id = "med-002",
            patientId = "pat-002",
            allergies = "None",
            lastUpdated = DateTimeUtil.currentInstant().toString()
        ),
        "pat-003" to MedicalHistory(
            id = "med-003",
            patientId = "pat-003",
            hasHypertension = true,
            currentMedications = "Concor 5mg",
            lastUpdated = DateTimeUtil.currentInstant().toString()
        )
    )

    private val groupsList = mutableListOf(
        PatientGroup(id = "grp-1", name = "VIP", colorHex = "#FFB300", description = "Priority patients"),
        PatientGroup(id = "grp-2", name = "Orthodontics", colorHex = "#1B6B93", description = "Active braces/aligners"),
        PatientGroup(id = "grp-3", name = "Endo", colorHex = "#8E24AA", description = "Root canal treatments"),
        PatientGroup(id = "grp-4", name = "Prostho", colorHex = "#00897B", description = "Crowns and bridges"),
        PatientGroup(id = "grp-5", name = "Pediatric", colorHex = "#E53935", description = "Children under 14")
    )

    private val patientsFlow = MutableStateFlow(seedPatients)

    override fun getPatients(): Flow<List<Patient>> = patientsFlow.asStateFlow()

    override suspend fun getPatientById(id: String): Result<Patient> {
        val patient = patientsFlow.value.find { it.id == id }
        return if (patient != null) {
            Result.Success(patient)
        } else {
            Result.Error("Patient not found with ID: $id")
        }
    }

    override suspend fun savePatient(patient: Patient): Result<Patient> {
        val current = patientsFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == patient.id }
        val saved = if (index >= 0) {
            current[index] = patient
            patient
        } else {
            val nextNumber = "P-${(current.size + 1).toString().padStart(5, '0')}"
            val newPatient = patient.copy(
                id = "pat-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                patientNumber = nextNumber
            )
            current.add(0, newPatient)
            newPatient
        }
        patientsFlow.value = current
        return Result.Success(saved)
    }

    override suspend fun deletePatient(id: String): Result<Unit> {
        val current = patientsFlow.value.toMutableList()
        current.removeAll { it.id == id }
        patientsFlow.value = current
        return Result.Success(Unit)
    }

    override suspend fun getMedicalHistory(patientId: String): Result<MedicalHistory> {
        val history = seedMedicalHistories.getOrPut(patientId) {
            MedicalHistory(
                id = "med-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                patientId = patientId
            )
        }
        return Result.Success(history)
    }

    override suspend fun saveMedicalHistory(history: MedicalHistory): Result<MedicalHistory> {
        val updated = history.copy(lastUpdated = DateTimeUtil.currentInstant().toString())
        seedMedicalHistories[history.patientId] = updated
        return Result.Success(updated)
    }

    override suspend fun getPatientGroups(): Result<List<PatientGroup>> {
        return Result.Success(groupsList.toList())
    }

    override suspend fun savePatientGroup(group: PatientGroup): Result<PatientGroup> {
        val index = groupsList.indexOfFirst { it.id == group.id }
        val saved = if (index >= 0) {
            groupsList[index] = group
            group
        } else {
            val newGroup = group.copy(id = "grp-${DateTimeUtil.currentInstant().toEpochMilliseconds()}")
            groupsList.add(newGroup)
            newGroup
        }
        return Result.Success(saved)
    }

    override suspend fun deletePatientGroup(id: String): Result<Unit> {
        groupsList.removeAll { it.id == id }
        return Result.Success(Unit)
    }
}
