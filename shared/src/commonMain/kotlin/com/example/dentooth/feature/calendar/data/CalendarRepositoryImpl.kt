package com.example.dentooth.feature.calendar.data

import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.calendar.domain.model.Appointment
import com.example.dentooth.feature.calendar.domain.model.AppointmentConflict
import com.example.dentooth.feature.calendar.domain.model.AppointmentStatus
import com.example.dentooth.feature.calendar.domain.model.AppointmentType
import com.example.dentooth.feature.calendar.domain.model.Chair
import com.example.dentooth.feature.calendar.domain.model.StaffDoctor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class CalendarRepositoryImpl : CalendarRepository {

    private val doctorsList = listOf(
        StaffDoctor(
            id = "doc-1",
            name = "Dr. Ahmed Mostafa",
            specialization = "Endodontics & Restorative",
            colorHex = 0xFF1976D2 // Deep Blue
        ),
        StaffDoctor(
            id = "doc-2",
            name = "Dr. Sarah Khalil",
            specialization = "Orthodontics & Pedodontics",
            colorHex = 0xFF7B1FA2 // Purple
        ),
        StaffDoctor(
            id = "doc-3",
            name = "Dr. Omar Tarek",
            specialization = "Oral & Maxillofacial Surgery",
            colorHex = 0xFF00796B // Teal
        )
    )

    private val chairsList = listOf(
        Chair(id = "chair-1", name = "Operatory 1 (Main Chair)", isActive = true),
        Chair(id = "chair-2", name = "Operatory 2 (Surgical & Endo)", isActive = true),
        Chair(id = "chair-3", name = "Operatory 3 (Pediatric/Ortho)", isActive = true)
    )

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    private val appointmentsFlow = _appointments.asStateFlow()

    init {
        seedInitialAppointments()
    }

    private fun seedInitialAppointments() {
        val today = DateTimeUtil.today()
        val todayStr = today.toString()

        val yestStr = LocalDate.fromEpochDays(today.toEpochDays() - 1).toString()
        val tomStr = LocalDate.fromEpochDays(today.toEpochDays() + 1).toString()
        val dayAfterStr = LocalDate.fromEpochDays(today.toEpochDays() + 2).toString()

        val initialList = listOf(
            // Today appointments
            Appointment(
                id = "apt-1",
                patientId = "1",
                patientName = "Ahmed Ali",
                patientPhone = "+201012345678",
                doctorId = "doc-1",
                doctorName = "Dr. Ahmed Mostafa",
                chairId = "chair-1",
                chairName = "Operatory 1 (Main Chair)",
                title = "Root Canal Treatment - Tooth #16",
                procedureName = "Molar Root Canal (1st Session)",
                toothNumber = 16,
                date = todayStr,
                startTime = "09:30",
                endTime = "10:30",
                status = AppointmentStatus.CONFIRMED,
                type = AppointmentType.TREATMENT,
                notes = "Patient experiencing severe sensitivity to hot liquids.",
                reminderSent = true
            ),
            Appointment(
                id = "apt-2",
                patientId = "2",
                patientName = "Fatma Hassan",
                patientPhone = "+201123456789",
                doctorId = "doc-2",
                doctorName = "Dr. Sarah Khalil",
                chairId = "chair-3",
                chairName = "Operatory 3 (Pediatric/Ortho)",
                title = "Orthodontic Wire Activation",
                procedureName = "Archwire Adjustment",
                toothNumber = null,
                date = todayStr,
                startTime = "10:30",
                endTime = "11:15",
                status = AppointmentStatus.IN_PROGRESS,
                type = AppointmentType.TREATMENT,
                notes = "Upper 0.016 NiTi wire replacement.",
                reminderSent = true
            ),
            Appointment(
                id = "apt-3",
                patientId = "3",
                patientName = "Mahmoud Ibrahim",
                patientPhone = "+201234567890",
                doctorId = "doc-3",
                doctorName = "Dr. Omar Tarek",
                chairId = "chair-2",
                chairName = "Operatory 2 (Surgical & Endo)",
                title = "Surgical Extraction - Impacted Wisdom Tooth",
                procedureName = "Surgical Extraction of #48",
                toothNumber = 48,
                date = todayStr,
                startTime = "12:00",
                endTime = "13:00",
                status = AppointmentStatus.SCHEDULED,
                type = AppointmentType.TREATMENT,
                notes = "Ensure patient took pre-op antibiotic prophylaxis.",
                reminderSent = true
            ),
            Appointment(
                id = "apt-4",
                patientId = "1",
                patientName = "Ahmed Ali",
                patientPhone = "+201012345678",
                doctorId = "doc-1",
                doctorName = "Dr. Ahmed Mostafa",
                chairId = "chair-1",
                chairName = "Operatory 1 (Main Chair)",
                title = "Post-Op Checkup & Suture Removal",
                procedureName = "Clinical Follow-up",
                toothNumber = null,
                date = todayStr,
                startTime = "14:00",
                endTime = "14:30",
                status = AppointmentStatus.SCHEDULED,
                type = AppointmentType.FOLLOW_UP,
                notes = "Quick suture check",
                reminderSent = false
            ),
            Appointment(
                id = "apt-5",
                patientId = "2",
                patientName = "Fatma Hassan",
                patientPhone = "+201123456789",
                doctorId = "doc-1",
                doctorName = "Dr. Ahmed Mostafa",
                chairId = "chair-1",
                chairName = "Operatory 1 (Main Chair)",
                title = "Emergency Acute Toothache",
                procedureName = "Emergency Pulp Exposure Care",
                toothNumber = 26,
                date = todayStr,
                startTime = "16:00",
                endTime = "16:45",
                status = AppointmentStatus.SCHEDULED,
                type = AppointmentType.EMERGENCY,
                notes = "Severe nocturnal throbbing pain",
                reminderSent = false
            ),

            // Yesterday appointments
            Appointment(
                id = "apt-y1",
                patientId = "1",
                patientName = "Ahmed Ali",
                patientPhone = "+201012345678",
                doctorId = "doc-1",
                doctorName = "Dr. Ahmed Mostafa",
                chairId = "chair-1",
                chairName = "Operatory 1 (Main Chair)",
                title = "Full Mouth Ultrasonic Scaling",
                procedureName = "Prophylaxis & Scaling",
                toothNumber = null,
                date = yestStr,
                startTime = "11:00",
                endTime = "11:45",
                status = AppointmentStatus.COMPLETED,
                type = AppointmentType.TREATMENT,
                notes = "Subgingival calculus thoroughly removed.",
                reminderSent = true
            ),

            // Tomorrow appointments
            Appointment(
                id = "apt-t1",
                patientId = "3",
                patientName = "Mahmoud Ibrahim",
                patientPhone = "+201234567890",
                doctorId = "doc-3",
                doctorName = "Dr. Omar Tarek",
                chairId = "chair-2",
                chairName = "Operatory 2 (Surgical & Endo)",
                title = "Implant Placement Planning & CBCT Review",
                procedureName = "Implant Consultation",
                toothNumber = 36,
                date = tomStr,
                startTime = "10:00",
                endTime = "11:00",
                status = AppointmentStatus.SCHEDULED,
                type = AppointmentType.CONSULTATION,
                notes = "Review mandibular bone density and canal distance.",
                reminderSent = true
            ),
            Appointment(
                id = "apt-t2",
                patientId = "2",
                patientName = "Fatma Hassan",
                patientPhone = "+201123456789",
                doctorId = "doc-1",
                doctorName = "Dr. Ahmed Mostafa",
                chairId = "chair-1",
                chairName = "Operatory 1 (Main Chair)",
                title = "Composite Restoration Class II - Tooth #24",
                procedureName = "MO Composite Filling",
                toothNumber = 24,
                date = tomStr,
                startTime = "13:00",
                endTime = "14:00",
                status = AppointmentStatus.SCHEDULED,
                type = AppointmentType.TREATMENT,
                notes = "Use shade A2",
                reminderSent = false
            ),

            // Day after tomorrow
            Appointment(
                id = "apt-d1",
                patientId = "1",
                patientName = "Ahmed Ali",
                patientPhone = "+201012345678",
                doctorId = "doc-1",
                doctorName = "Dr. Ahmed Mostafa",
                chairId = "chair-1",
                chairName = "Operatory 1 (Main Chair)",
                title = "Root Canal Obturation - Tooth #16",
                procedureName = "Endodontic Obturation",
                toothNumber = 16,
                date = dayAfterStr,
                startTime = "11:00",
                endTime = "12:00",
                status = AppointmentStatus.SCHEDULED,
                type = AppointmentType.TREATMENT,
                notes = "Warm vertical compaction technique.",
                reminderSent = false
            )
        )

        _appointments.value = initialList
    }

    override fun getAppointments(): Flow<List<Appointment>> = appointmentsFlow

    override fun getAppointmentsForDate(date: String): Flow<List<Appointment>> {
        return appointmentsFlow.map { list ->
            list.filter { it.date == date }.sortedBy { it.startTime }
        }
    }

    override fun getAppointmentsForPatient(patientId: String): Flow<List<Appointment>> {
        return appointmentsFlow.map { list ->
            list.filter { it.patientId == patientId }.sortedByDescending { it.date }
        }
    }

    override fun getChairs(): Flow<List<Chair>> = MutableStateFlow(chairsList).asStateFlow()

    override fun getDoctors(): Flow<List<StaffDoctor>> = MutableStateFlow(doctorsList).asStateFlow()

    override suspend fun createAppointment(appointment: Appointment): Result<Appointment> {
        // Check conflict before creating
        val conflict = checkConflict(
            doctorId = appointment.doctorId,
            chairId = appointment.chairId,
            date = appointment.date,
            startTime = appointment.startTime,
            endTime = appointment.endTime
        )

        if (conflict.hasConflict) {
            return Result.failure(IllegalStateException(conflict.message ?: "Appointment conflict detected"))
        }

        val updated = _appointments.value + appointment
        _appointments.value = updated
        return Result.success(appointment)
    }

    override suspend fun updateAppointment(appointment: Appointment): Result<Appointment> {
        val conflict = checkConflict(
            doctorId = appointment.doctorId,
            chairId = appointment.chairId,
            date = appointment.date,
            startTime = appointment.startTime,
            endTime = appointment.endTime,
            excludeAppointmentId = appointment.id
        )

        if (conflict.hasConflict) {
            return Result.failure(IllegalStateException(conflict.message ?: "Appointment conflict detected"))
        }

        val current = _appointments.value
        val index = current.indexOfFirst { it.id == appointment.id }
        if (index != -1) {
            val updated = current.toMutableList()
            updated[index] = appointment
            _appointments.value = updated
            return Result.success(appointment)
        }
        return Result.failure(IllegalArgumentException("Appointment not found"))
    }

    override suspend fun updateStatus(appointmentId: String, status: AppointmentStatus): Result<Unit> {
        val current = _appointments.value
        val index = current.indexOfFirst { it.id == appointmentId }
        if (index != -1) {
            val updated = current.toMutableList()
            val existing = updated[index]
            updated[index] = existing.copy(status = status)
            _appointments.value = updated
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException("Appointment not found"))
    }

    override suspend fun deleteAppointment(appointmentId: String): Result<Unit> {
        _appointments.value = _appointments.value.filter { it.id != appointmentId }
        return Result.success(Unit)
    }

    override suspend fun checkConflict(
        doctorId: String,
        chairId: String?,
        date: String,
        startTime: String,
        endTime: String,
        excludeAppointmentId: String?
    ): AppointmentConflict {
        fun timeToMinutes(t: String): Int {
            return try {
                val parts = t.split(":")
                parts[0].toInt() * 60 + parts[1].toInt()
            } catch (e: Exception) {
                0
            }
        }

        val newStart = timeToMinutes(startTime)
        val newEnd = timeToMinutes(endTime)

        if (newEnd <= newStart) {
            return AppointmentConflict(
                hasConflict = true,
                message = "End time must be after start time."
            )
        }

        val sameDateAppointments = _appointments.value.filter {
            it.date == date &&
            it.id != excludeAppointmentId &&
            it.status != AppointmentStatus.CANCELLED
        }

        for (existing in sameDateAppointments) {
            val existStart = timeToMinutes(existing.startTime)
            val existEnd = timeToMinutes(existing.endTime)

            // Overlap check: startA < endB && endA > startB
            val overlaps = newStart < existEnd && newEnd > existStart

            if (overlaps) {
                if (existing.doctorId == doctorId) {
                    return AppointmentConflict(
                        hasConflict = true,
                        message = "Conflict: ${existing.doctorName} is already booked from ${existing.startTime} to ${existing.endTime} with patient ${existing.patientName}.",
                        conflictingAppointment = existing
                    )
                }

                if (chairId != null && existing.chairId != null && existing.chairId == chairId) {
                    return AppointmentConflict(
                        hasConflict = true,
                        message = "Conflict: ${existing.chairName} is already occupied from ${existing.startTime} to ${existing.endTime}.",
                        conflictingAppointment = existing
                    )
                }
            }
        }

        return AppointmentConflict(hasConflict = false)
    }
}
