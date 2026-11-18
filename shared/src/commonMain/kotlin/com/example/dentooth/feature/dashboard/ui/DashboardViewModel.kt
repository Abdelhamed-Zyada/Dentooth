package com.example.dentooth.feature.dashboard.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.auth.data.AuthRepository
import com.example.dentooth.core.auth.domain.UserSession
import com.example.dentooth.feature.patient.data.PatientRepository
import com.example.dentooth.feature.patient.domain.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardAppointment(
    val id: String,
    val patientName: String,
    val patientNumber: String,
    val time: String,
    val procedure: String,
    val chair: String,
    val status: String
)

data class DashboardUiState(
    val userSession: UserSession? = null,
    val totalPatients: Int = 0,
    val todayAppointmentsCount: Int = 8,
    val todayRevenue: Double = 14500.0,
    val pendingInvoicesCount: Int = 3,
    val lowStockItemsCount: Int = 2,
    val todayAppointments: List<DashboardAppointment> = emptyList(),
    val recentPatients: List<Patient> = emptyList()
)

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val patientRepository: PatientRepository
) : ScreenModel {

    private val seedAppointments = listOf(
        DashboardAppointment(
            id = "apt-1",
            patientName = "Ahmed Hassan",
            patientNumber = "P-00001",
            time = "10:00 AM",
            procedure = "Root Canal Treatment - #16",
            chair = "Chair 1",
            status = "Scheduled"
        ),
        DashboardAppointment(
            id = "apt-2",
            patientName = "Sara Mohamed",
            patientNumber = "P-00002",
            time = "11:30 AM",
            procedure = "Orthodontic Wire Activation",
            chair = "Chair 2",
            status = "Confirmed"
        ),
        DashboardAppointment(
            id = "apt-3",
            patientName = "Mahmoud Tarek",
            patientNumber = "P-00003",
            time = "01:00 PM",
            procedure = "Zirconia Crown Placement",
            chair = "Chair 1",
            status = "In Progress"
        ),
        DashboardAppointment(
            id = "apt-4",
            patientName = "Omar Khaled",
            patientNumber = "P-00005",
            time = "03:30 PM",
            procedure = "Implant Post-op Checkup",
            chair = "Chair 1",
            status = "Scheduled"
        )
    )

    val state: StateFlow<DashboardUiState> = combine(
        authRepository.currentSession,
        patientRepository.getPatients()
    ) { session, patients ->
        DashboardUiState(
            userSession = session,
            totalPatients = patients.size,
            todayAppointmentsCount = seedAppointments.size,
            todayRevenue = 14500.0,
            pendingInvoicesCount = 3,
            lowStockItemsCount = 2,
            todayAppointments = seedAppointments,
            recentPatients = patients.take(4)
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}
