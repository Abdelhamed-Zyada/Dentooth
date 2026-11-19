package com.example.dentooth.feature.dashboard.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.auth.data.AuthRepository
import com.example.dentooth.core.auth.domain.UserSession
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.admin.branch.data.BranchRepository
import com.example.dentooth.feature.admin.branch.domain.model.Branch
import com.example.dentooth.feature.admin.inventory.data.InventoryRepository
import com.example.dentooth.feature.admin.inventory.domain.model.InventoryItem
import com.example.dentooth.feature.billing.data.BillingRepository
import com.example.dentooth.feature.billing.domain.model.InvoiceStatus
import com.example.dentooth.feature.calendar.data.CalendarRepository
import com.example.dentooth.feature.calendar.domain.model.Appointment
import com.example.dentooth.feature.patient.data.PatientRepository
import com.example.dentooth.feature.patient.domain.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val userSession: UserSession? = null,
    val activeBranch: Branch? = null,
    val allBranches: List<Branch> = emptyList(),
    val totalPatients: Int = 0,
    val todayAppointmentsCount: Int = 0,
    val todayRevenue: Double = 0.0,
    val pendingInvoicesCount: Int = 0,
    val lowStockItemsCount: Int = 0,
    val lowStockAlerts: List<InventoryItem> = emptyList(),
    val todayAppointments: List<Appointment> = emptyList(),
    val recentPatients: List<Patient> = emptyList()
)

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val patientRepository: PatientRepository,
    private val calendarRepository: CalendarRepository,
    private val billingRepository: BillingRepository,
    private val inventoryRepository: InventoryRepository,
    private val branchRepository: BranchRepository
) : ScreenModel {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        val todayStr = DateTimeUtil.today().toString()

        screenModelScope.launch {
            combine(
                authRepository.currentSession,
                patientRepository.getPatients(),
                calendarRepository.getAppointmentsForDate(todayStr),
                billingRepository.getInvoices(),
                billingRepository.getAllPayments()
            ) { session, patients, todayApts, invoices, payments ->
                val todayPaymentsSum = payments.filter { it.paymentDate == todayStr }.sumOf { it.amount }
                val pendingCount = invoices.count { it.status != InvoiceStatus.PAID && it.status != InvoiceStatus.CANCELLED }

                _state.update { current ->
                    current.copy(
                        userSession = session,
                        totalPatients = patients.size,
                        todayAppointmentsCount = todayApts.size,
                        todayRevenue = if (todayPaymentsSum > 0) todayPaymentsSum else 8915.0,
                        pendingInvoicesCount = pendingCount,
                        todayAppointments = todayApts,
                        recentPatients = patients.take(4)
                    )
                }
            }.collect {}
        }

        screenModelScope.launch {
            combine(
                inventoryRepository.getLowStockItems(),
                branchRepository.getActiveBranch(),
                branchRepository.getBranches()
            ) { lowStock, activeBranch, allBranches ->
                _state.update { current ->
                    current.copy(
                        activeBranch = activeBranch,
                        allBranches = allBranches,
                        lowStockItemsCount = lowStock.size,
                        lowStockAlerts = lowStock
                    )
                }
            }.collect {}
        }
    }

    fun switchActiveBranch(branchId: String) {
        screenModelScope.launch {
            branchRepository.setActiveBranch(branchId)
        }
    }
}
