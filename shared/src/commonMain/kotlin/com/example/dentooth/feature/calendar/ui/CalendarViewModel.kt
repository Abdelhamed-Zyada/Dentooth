package com.example.dentooth.feature.calendar.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.core.util.UiState
import com.example.dentooth.feature.calendar.data.CalendarRepository
import com.example.dentooth.feature.calendar.domain.model.Appointment
import com.example.dentooth.feature.calendar.domain.model.AppointmentConflict
import com.example.dentooth.feature.calendar.domain.model.AppointmentStatus
import com.example.dentooth.feature.calendar.domain.model.AppointmentType
import com.example.dentooth.feature.calendar.domain.model.Chair
import com.example.dentooth.feature.calendar.domain.model.StaffDoctor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

enum class CalendarViewMode(val title: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    AGENDA("Agenda")
}

enum class CalendarColumnMode(val title: String) {
    BY_CHAIR("By Chair"),
    BY_DOCTOR("By Doctor")
}

data class AppointmentBookingPrefill(
    val date: String,
    val startTime: String = "09:00",
    val chairId: String? = null,
    val doctorId: String? = null,
    val patientId: String? = null,
    val patientName: String? = null
)

data class CalendarUiState(
    val selectedDate: LocalDate = DateTimeUtil.today(),
    val viewMode: CalendarViewMode = CalendarViewMode.DAY,
    val columnMode: CalendarColumnMode = CalendarColumnMode.BY_CHAIR,
    val appointments: UiState<List<Appointment>> = UiState.Loading,
    val allAppointments: List<Appointment> = emptyList(),
    val chairs: List<Chair> = emptyList(),
    val doctors: List<StaffDoctor> = emptyList(),
    val doctorFilterId: String? = null,
    val chairFilterId: String? = null,
    val statusFilter: AppointmentStatus? = null,
    val selectedAppointmentForDetail: Appointment? = null,
    val bookingPrefill: AppointmentBookingPrefill? = null,
    val isBookingSheetOpen: Boolean = false,
    val errorMessage: String? = null
)

class CalendarViewModel(
    private val calendarRepository: CalendarRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            combine(
                calendarRepository.getAppointments(),
                calendarRepository.getChairs(),
                calendarRepository.getDoctors()
            ) { apts, chairs, doctors ->
                Triple(apts, chairs, doctors)
            }.collect { (apts, chairs, doctors) ->
                _uiState.update { current ->
                    current.copy(
                        allAppointments = apts,
                        appointments = UiState.Success(filterAppointments(apts, current)),
                        chairs = chairs,
                        doctors = doctors
                    )
                }
            }
        }
    }

    private fun filterAppointments(list: List<Appointment>, state: CalendarUiState): List<Appointment> {
        val selectedDateStr = state.selectedDate.toString()
        return list.filter { apt ->
            val matchDate = when (state.viewMode) {
                CalendarViewMode.DAY -> apt.date == selectedDateStr
                CalendarViewMode.WEEK -> isDateInWeek(apt.date, state.selectedDate)
                CalendarViewMode.MONTH -> isDateInMonth(apt.date, state.selectedDate)
                CalendarViewMode.AGENDA -> true // Agenda shows all upcoming or surrounding
            }

            val matchDoc = state.doctorFilterId == null || apt.doctorId == state.doctorFilterId
            val matchChair = state.chairFilterId == null || apt.chairId == state.chairFilterId
            val matchStatus = state.statusFilter == null || apt.status == state.statusFilter

            matchDate && matchDoc && matchChair && matchStatus
        }.sortedWith(compareBy({ it.date }, { it.startTime }))
    }

    private fun isDateInWeek(dateStr: String, reference: LocalDate): Boolean {
        return try {
            val parts = dateStr.split("-").map { it.toInt() }
            val aptDate = LocalDate(parts[0], parts[1], parts[2])
            val diff = aptDate.toEpochDays() - reference.toEpochDays()
            diff in -3..3 // +/- 3 days window around selected day
        } catch (e: Exception) {
            false
        }
    }

    private fun isDateInMonth(dateStr: String, reference: LocalDate): Boolean {
        return try {
            val parts = dateStr.split("-").map { it.toInt() }
            parts[0] == reference.year && parts[1] == reference.monthNumber
        } catch (e: Exception) {
            false
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _uiState.update { current ->
            val nextState = current.copy(selectedDate = date)
            nextState.copy(appointments = UiState.Success(filterAppointments(nextState.allAppointments, nextState)))
        }
    }

    fun goToToday() {
        setSelectedDate(DateTimeUtil.today())
    }

    fun nextDate() {
        val current = _uiState.value
        val next = when (current.viewMode) {
            CalendarViewMode.DAY -> LocalDate.fromEpochDays(current.selectedDate.toEpochDays() + 1)
            CalendarViewMode.WEEK -> LocalDate.fromEpochDays(current.selectedDate.toEpochDays() + 7)
            CalendarViewMode.MONTH -> {
                val nextMonth = if (current.selectedDate.monthNumber == 12) 1 else current.selectedDate.monthNumber + 1
                val nextYear = if (current.selectedDate.monthNumber == 12) current.selectedDate.year + 1 else current.selectedDate.year
                LocalDate(nextYear, nextMonth, 1)
            }
            CalendarViewMode.AGENDA -> LocalDate.fromEpochDays(current.selectedDate.toEpochDays() + 1)
        }
        setSelectedDate(next)
    }

    fun previousDate() {
        val current = _uiState.value
        val prev = when (current.viewMode) {
            CalendarViewMode.DAY -> LocalDate.fromEpochDays(current.selectedDate.toEpochDays() - 1)
            CalendarViewMode.WEEK -> LocalDate.fromEpochDays(current.selectedDate.toEpochDays() - 7)
            CalendarViewMode.MONTH -> {
                val prevMonth = if (current.selectedDate.monthNumber == 1) 12 else current.selectedDate.monthNumber - 1
                val prevYear = if (current.selectedDate.monthNumber == 1) current.selectedDate.year - 1 else current.selectedDate.year
                LocalDate(prevYear, prevMonth, 1)
            }
            CalendarViewMode.AGENDA -> LocalDate.fromEpochDays(current.selectedDate.toEpochDays() - 1)
        }
        setSelectedDate(prev)
    }

    fun setViewMode(mode: CalendarViewMode) {
        _uiState.update { current ->
            val nextState = current.copy(viewMode = mode)
            nextState.copy(appointments = UiState.Success(filterAppointments(nextState.allAppointments, nextState)))
        }
    }

    fun setColumnMode(mode: CalendarColumnMode) {
        _uiState.update { it.copy(columnMode = mode) }
    }

    fun setDoctorFilter(doctorId: String?) {
        _uiState.update { current ->
            val nextState = current.copy(doctorFilterId = doctorId)
            nextState.copy(appointments = UiState.Success(filterAppointments(nextState.allAppointments, nextState)))
        }
    }

    fun setChairFilter(chairId: String?) {
        _uiState.update { current ->
            val nextState = current.copy(chairFilterId = chairId)
            nextState.copy(appointments = UiState.Success(filterAppointments(nextState.allAppointments, nextState)))
        }
    }

    fun setStatusFilter(status: AppointmentStatus?) {
        _uiState.update { current ->
            val nextState = current.copy(statusFilter = status)
            nextState.copy(appointments = UiState.Success(filterAppointments(nextState.allAppointments, nextState)))
        }
    }

    fun openDetail(appointment: Appointment) {
        _uiState.update { it.copy(selectedAppointmentForDetail = appointment) }
    }

    fun closeDetail() {
        _uiState.update { it.copy(selectedAppointmentForDetail = null) }
    }

    fun openBookingSheet(prefill: AppointmentBookingPrefill? = null) {
        val effectivePrefill = prefill ?: AppointmentBookingPrefill(date = _uiState.value.selectedDate.toString())
        _uiState.update { it.copy(isBookingSheetOpen = true, bookingPrefill = effectivePrefill, errorMessage = null) }
    }

    fun closeBookingSheet() {
        _uiState.update { it.copy(isBookingSheetOpen = false, bookingPrefill = null, errorMessage = null) }
    }

    fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus) {
        screenModelScope.launch {
            calendarRepository.updateStatus(appointmentId, status)
            _uiState.update { current ->
                if (current.selectedAppointmentForDetail?.id == appointmentId) {
                    current.copy(selectedAppointmentForDetail = current.selectedAppointmentForDetail.copy(status = status))
                } else current
            }
        }
    }

    fun deleteAppointment(appointmentId: String) {
        screenModelScope.launch {
            calendarRepository.deleteAppointment(appointmentId)
            closeDetail()
        }
    }

    fun bookAppointment(
        patientId: String,
        patientName: String,
        patientPhone: String,
        doctorId: String,
        doctorName: String,
        chairId: String?,
        chairName: String?,
        title: String,
        procedureName: String?,
        toothNumber: Int?,
        date: String,
        startTime: String,
        endTime: String,
        type: AppointmentType,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        screenModelScope.launch {
            val id = "apt-${DateTimeUtil.currentInstant().toEpochMilliseconds()}"
            val newApt = Appointment(
                id = id,
                patientId = patientId,
                patientName = patientName,
                patientPhone = patientPhone,
                doctorId = doctorId,
                doctorName = doctorName,
                chairId = chairId,
                chairName = chairName,
                procedureName = procedureName,
                toothNumber = toothNumber,
                title = title,
                date = date,
                startTime = startTime,
                endTime = endTime,
                status = AppointmentStatus.SCHEDULED,
                type = type,
                notes = notes,
                reminderSent = false
            )

            val result = calendarRepository.createAppointment(newApt)
            result.onSuccess {
                closeBookingSheet()
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(errorMessage = err.message ?: "Failed to book appointment") }
            }
        }
    }

    suspend fun checkConflict(
        doctorId: String,
        chairId: String?,
        date: String,
        startTime: String,
        endTime: String,
        excludeId: String? = null
    ): AppointmentConflict {
        return calendarRepository.checkConflict(doctorId, chairId, date, startTime, endTime, excludeId)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
