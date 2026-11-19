package com.example.dentooth.feature.calendar.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.calendar.domain.model.AppointmentStatus
import com.example.dentooth.feature.calendar.ui.components.AgendaView
import com.example.dentooth.feature.calendar.ui.components.AppointmentBookingSheet
import com.example.dentooth.feature.calendar.ui.components.AppointmentDetailDialog
import com.example.dentooth.feature.calendar.ui.components.DayTimelineView
import com.example.dentooth.feature.calendar.ui.components.MonthCalendarView
import com.example.dentooth.feature.calendar.ui.components.WeekCalendarView

class CalendarScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<CalendarViewModel>()
        val state by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                viewModel.clearError()
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Column {
                    DentoothTopBar(
                        title = "Calendar & Reservations",
                        actions = {
                            TextButton(onClick = { viewModel.goToToday() }) {
                                Text("Today", fontWeight = FontWeight.Bold)
                            }
                        }
                    )

                    // Date Navigator Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.previousDate() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                        }

                        Text(
                            text = when (state.viewMode) {
                                CalendarViewMode.DAY -> DateTimeUtil.formatDate(state.selectedDate)
                                CalendarViewMode.WEEK -> "Week of ${DateTimeUtil.formatDate(state.selectedDate)}"
                                CalendarViewMode.MONTH -> "${getMonthName(state.selectedDate.monthNumber)} ${state.selectedDate.year}"
                                CalendarViewMode.AGENDA -> "Schedule Agenda"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { viewModel.nextDate() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                        }
                    }

                    // View Mode Tabs: Day, Week, Month, Agenda
                    PrimaryTabRow(
                        selectedTabIndex = state.viewMode.ordinal,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        CalendarViewMode.entries.forEach { mode ->
                            Tab(
                                selected = state.viewMode == mode,
                                onClick = { viewModel.setViewMode(mode) },
                                text = { Text(mode.title, fontWeight = FontWeight.SemiBold) }
                            )
                        }
                    }

                    // Sub-controls / Filters row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Day column toggle (only in Day view)
                        if (state.viewMode == CalendarViewMode.DAY) {
                            FilterChip(
                                selected = state.columnMode == CalendarColumnMode.BY_CHAIR,
                                onClick = { viewModel.setColumnMode(CalendarColumnMode.BY_CHAIR) },
                                label = { Text("By Chair") }
                            )
                            FilterChip(
                                selected = state.columnMode == CalendarColumnMode.BY_DOCTOR,
                                onClick = { viewModel.setColumnMode(CalendarColumnMode.BY_DOCTOR) },
                                label = { Text("By Doctor") }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        // Doctor filter chips
                        FilterChip(
                            selected = state.doctorFilterId == null,
                            onClick = { viewModel.setDoctorFilter(null) },
                            label = { Text("All Doctors") }
                        )
                        state.doctors.forEach { doc ->
                            FilterChip(
                                selected = state.doctorFilterId == doc.id,
                                onClick = {
                                    viewModel.setDoctorFilter(if (state.doctorFilterId == doc.id) null else doc.id)
                                },
                                label = { Text(doc.name.replace("Dr. ", "")) }
                            )
                        }

                        // Status filter chips
                        FilterChip(
                            selected = state.statusFilter == null,
                            onClick = { viewModel.setStatusFilter(null) },
                            label = { Text("All Statuses") }
                        )
                        AppointmentStatus.entries.forEach { status ->
                            FilterChip(
                                selected = state.statusFilter == status,
                                onClick = {
                                    viewModel.setStatusFilter(if (state.statusFilter == status) null else status)
                                },
                                label = { Text(status.label) }
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openBookingSheet() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Book Appointment")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.appointments.isLoading) {
                    DentoothLoadingState(message = "Loading schedule...")
                } else {
                    val apts = state.appointments.dataOrNull() ?: emptyList()

                    when (state.viewMode) {
                        CalendarViewMode.DAY -> {
                            DayTimelineView(
                                dateStr = state.selectedDate.toString(),
                                columnMode = state.columnMode,
                                chairs = state.chairs,
                                doctors = state.doctors,
                                appointments = apts,
                                onAppointmentClick = { viewModel.openDetail(it) },
                                onEmptySlotClick = { prefill -> viewModel.openBookingSheet(prefill) }
                            )
                        }
                        CalendarViewMode.WEEK -> {
                            WeekCalendarView(
                                referenceDate = state.selectedDate,
                                appointments = state.allAppointments,
                                onSelectDate = { date ->
                                    viewModel.setSelectedDate(date)
                                    viewModel.setViewMode(CalendarViewMode.DAY)
                                },
                                onAppointmentClick = { viewModel.openDetail(it) }
                            )
                        }
                        CalendarViewMode.MONTH -> {
                            MonthCalendarView(
                                selectedDate = state.selectedDate,
                                appointments = state.allAppointments,
                                onSelectDate = { date -> viewModel.setSelectedDate(date) },
                                onAppointmentClick = { viewModel.openDetail(it) }
                            )
                        }
                        CalendarViewMode.AGENDA -> {
                            AgendaView(
                                appointments = apts,
                                onAppointmentClick = { viewModel.openDetail(it) },
                                onStatusChange = { id, status -> viewModel.updateAppointmentStatus(id, status) }
                            )
                        }
                    }
                }
            }

            // Appointment Detail Dialog
            state.selectedAppointmentForDetail?.let { apt ->
                AppointmentDetailDialog(
                    appointment = apt,
                    onDismiss = { viewModel.closeDetail() },
                    onStatusChange = { status -> viewModel.updateAppointmentStatus(apt.id, status) },
                    onDelete = { viewModel.deleteAppointment(apt.id) }
                )
            }

            // Booking Bottom Sheet
            if (state.isBookingSheetOpen) {
                AppointmentBookingSheet(
                    prefill = state.bookingPrefill,
                    chairs = state.chairs,
                    doctors = state.doctors,
                    onDismiss = { viewModel.closeBookingSheet() },
                    onConflictCheck = { docId, chairId, date, start, end ->
                        viewModel.checkConflict(docId, chairId, date, start, end)
                    },
                    onBook = { patientId, patientName, patientPhone, doctorId, doctorName, chairId, chairName, title, procedureName, toothNum, date, start, end, type, notes ->
                        viewModel.bookAppointment(
                            patientId = patientId,
                            patientName = patientName,
                            patientPhone = patientPhone,
                            doctorId = doctorId,
                            doctorName = doctorName,
                            chairId = chairId,
                            chairName = chairName,
                            title = title,
                            procedureName = procedureName,
                            toothNumber = toothNum,
                            date = date,
                            startTime = start,
                            endTime = end,
                            type = type,
                            notes = notes,
                            onSuccess = {}
                        )
                    }
                )
            }
        }
    }

    private fun getMonthName(monthNumber: Int): String {
        val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        return months.getOrElse(monthNumber - 1) { "" }
    }
}
