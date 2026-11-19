package com.example.dentooth.feature.calendar.data

import com.example.dentooth.feature.calendar.domain.model.Appointment
import com.example.dentooth.feature.calendar.domain.model.AppointmentConflict
import com.example.dentooth.feature.calendar.domain.model.AppointmentStatus
import com.example.dentooth.feature.calendar.domain.model.Chair
import com.example.dentooth.feature.calendar.domain.model.StaffDoctor
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getAppointments(): Flow<List<Appointment>>
    fun getAppointmentsForDate(date: String): Flow<List<Appointment>>
    fun getAppointmentsForPatient(patientId: String): Flow<List<Appointment>>
    fun getChairs(): Flow<List<Chair>>
    fun getDoctors(): Flow<List<StaffDoctor>>
    suspend fun createAppointment(appointment: Appointment): Result<Appointment>
    suspend fun updateAppointment(appointment: Appointment): Result<Appointment>
    suspend fun updateStatus(appointmentId: String, status: AppointmentStatus): Result<Unit>
    suspend fun deleteAppointment(appointmentId: String): Result<Unit>
    suspend fun checkConflict(
        doctorId: String,
        chairId: String?,
        date: String,
        startTime: String,
        endTime: String,
        excludeAppointmentId: String? = null
    ): AppointmentConflict
}
