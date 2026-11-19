package com.example.dentooth.feature.admin.staff.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.admin.staff.data.StaffRepository
import com.example.dentooth.feature.admin.staff.domain.model.AttendanceRecord
import com.example.dentooth.feature.admin.staff.domain.model.AttendanceStatus
import com.example.dentooth.feature.admin.staff.domain.model.StaffMember
import com.example.dentooth.feature.admin.staff.domain.model.StaffRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StaffUiState(
    val staffList: UiState<List<StaffMember>> = UiState.Loading,
    val allStaff: List<StaffMember> = emptyList(),
    val attendanceRecords: List<AttendanceRecord> = emptyList(),
    val selectedRoleFilter: StaffRole? = null,
    val searchQuery: String = "",
    val staffToLogAttendance: StaffMember? = null,
    val isAddStaffOpen: Boolean = false,
    val activeTab: Int = 0 // 0: Staff Directory, 1: Attendance Log
)

class StaffViewModel(
    private val staffRepository: StaffRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(StaffUiState())
    val uiState: StateFlow<StaffUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val todayStr = DateTimeUtil.today().toString()
        screenModelScope.launch {
            combine(
                staffRepository.getStaff(),
                staffRepository.getAttendanceForDate(todayStr)
            ) { staff, attendance ->
                Pair(staff, attendance)
            }.collect { (staff, attendance) ->
                _uiState.update { current ->
                    current.copy(
                        allStaff = staff,
                        attendanceRecords = attendance,
                        staffList = UiState.Success(filterStaff(staff, current.selectedRoleFilter, current.searchQuery))
                    )
                }
            }
        }
    }

    private fun filterStaff(list: List<StaffMember>, role: StaffRole?, query: String): List<StaffMember> {
        val q = query.trim().lowercase()
        return list.filter { member ->
            val matchRole = role == null || member.role == role
            val matchQuery = q.isEmpty() ||
                    member.fullName.lowercase().contains(q) ||
                    (member.specialization?.lowercase()?.contains(q) ?: false)
            matchRole && matchQuery
        }
    }

    fun setActiveTab(tab: Int) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun setRoleFilter(role: StaffRole?) {
        _uiState.update { current ->
            val next = current.copy(selectedRoleFilter = role)
            next.copy(staffList = UiState.Success(filterStaff(next.allStaff, next.selectedRoleFilter, next.searchQuery)))
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            val next = current.copy(searchQuery = query)
            next.copy(staffList = UiState.Success(filterStaff(next.allStaff, next.selectedRoleFilter, next.searchQuery)))
        }
    }

    fun openLogAttendance(member: StaffMember) {
        _uiState.update { it.copy(staffToLogAttendance = member) }
    }

    fun closeLogAttendance() {
        _uiState.update { it.copy(staffToLogAttendance = null) }
    }

    fun logAttendance(
        staffId: String,
        staffName: String,
        checkIn: String?,
        checkOut: String?,
        status: AttendanceStatus,
        notes: String?
    ) {
        screenModelScope.launch {
            val record = AttendanceRecord(
                id = "att-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                staffId = staffId,
                staffName = staffName,
                date = DateTimeUtil.today().toString(),
                checkInTime = checkIn,
                checkOutTime = checkOut,
                status = status,
                notes = notes
            )
            staffRepository.logAttendance(record)
            closeLogAttendance()
        }
    }

    fun openAddStaff() {
        _uiState.update { it.copy(isAddStaffOpen = true) }
    }

    fun closeAddStaff() {
        _uiState.update { it.copy(isAddStaffOpen = false) }
    }

    fun addStaff(
        fullName: String,
        role: StaffRole,
        specialization: String?,
        license: String?,
        phone: String,
        email: String,
        salary: Double
    ) {
        screenModelScope.launch {
            val newMember = StaffMember(
                id = "staff-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                fullName = fullName,
                role = role,
                specialization = specialization,
                licenseNumber = license,
                phone = phone,
                email = email,
                hireDate = DateTimeUtil.today().toString(),
                salary = salary
            )
            staffRepository.addStaff(newMember)
            closeAddStaff()
        }
    }

    fun deleteStaff(staffId: String) {
        screenModelScope.launch {
            staffRepository.deleteStaff(staffId)
        }
    }
}
