package com.example.dentooth.feature.admin.staff.data

import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.admin.staff.domain.model.AttendanceRecord
import com.example.dentooth.feature.admin.staff.domain.model.AttendanceStatus
import com.example.dentooth.feature.admin.staff.domain.model.StaffMember
import com.example.dentooth.feature.admin.staff.domain.model.StaffRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class StaffRepositoryImpl : StaffRepository {

    private val _staff = MutableStateFlow<List<StaffMember>>(emptyList())
    private val _attendance = MutableStateFlow<List<AttendanceRecord>>(emptyList())

    init {
        seedInitialStaff()
    }

    private fun seedInitialStaff() {
        val todayStr = DateTimeUtil.today().toString()

        val sampleStaff = listOf(
            StaffMember(
                id = "staff-1",
                fullName = "Dr. Ahmed Mostafa",
                role = StaffRole.DOCTOR,
                specialization = "Endodontics & Restorative Dentistry",
                licenseNumber = "EGY-DEN-49102",
                phone = "+201012345678",
                email = "ahmed.mostafa@dentooth.com",
                branchName = "Main Clinic (New Cairo)",
                hireDate = "2024-01-15",
                salary = 28000.0,
                isPresentToday = true,
                activeCalendarColorHex = 0xFF1976D2
            ),
            StaffMember(
                id = "staff-2",
                fullName = "Dr. Sarah Khalil",
                role = StaffRole.DOCTOR,
                specialization = "Orthodontics & Pedodontics",
                licenseNumber = "EGY-DEN-58291",
                phone = "+201123456789",
                email = "sarah.khalil@dentooth.com",
                branchName = "Main Clinic (New Cairo)",
                hireDate = "2024-03-01",
                salary = 26000.0,
                isPresentToday = true,
                activeCalendarColorHex = 0xFF7B1FA2
            ),
            StaffMember(
                id = "staff-3",
                fullName = "Dr. Omar Tarek",
                role = StaffRole.DOCTOR,
                specialization = "Oral & Maxillofacial Surgery",
                licenseNumber = "EGY-DEN-61928",
                phone = "+201234567890",
                email = "omar.tarek@dentooth.com",
                branchName = "Maadi Specialized Center",
                hireDate = "2024-06-10",
                salary = 30000.0,
                isPresentToday = true,
                activeCalendarColorHex = 0xFF00796B
            ),
            StaffMember(
                id = "staff-4",
                fullName = "Nour El-Din",
                role = StaffRole.DENTAL_ASSISTANT,
                specialization = "Chairside Assisting & Sterilization",
                phone = "+201098472910",
                email = "nour.assistant@dentooth.com",
                branchName = "Main Clinic (New Cairo)",
                hireDate = "2024-02-01",
                salary = 9500.0,
                isPresentToday = true,
                activeCalendarColorHex = 0xFF00796B
            ),
            StaffMember(
                id = "staff-5",
                fullName = "Rania Sameh",
                role = StaffRole.RECEPTIONIST,
                specialization = "Patient Concierge & Scheduling",
                phone = "+201182736451",
                email = "rania.reception@dentooth.com",
                branchName = "Main Clinic (New Cairo)",
                hireDate = "2024-02-15",
                salary = 8500.0,
                isPresentToday = true,
                activeCalendarColorHex = 0xFF7B1FA2
            ),
            StaffMember(
                id = "staff-6",
                fullName = "Hany Youssef",
                role = StaffRole.ACCOUNTANT,
                specialization = "Billing, Invoicing & Tax Filing",
                phone = "+201229384756",
                email = "hany.finance@dentooth.com",
                branchName = "Main Clinic (New Cairo)",
                hireDate = "2024-04-01",
                salary = 11000.0,
                isPresentToday = false,
                activeCalendarColorHex = 0xFFE65100
            )
        )

        val sampleAttendance = listOf(
            AttendanceRecord(
                id = "att-1",
                staffId = "staff-1",
                staffName = "Dr. Ahmed Mostafa",
                date = todayStr,
                checkInTime = "08:45 AM",
                checkOutTime = null,
                status = AttendanceStatus.PRESENT
            ),
            AttendanceRecord(
                id = "att-2",
                staffId = "staff-2",
                staffName = "Dr. Sarah Khalil",
                date = todayStr,
                checkInTime = "09:15 AM",
                checkOutTime = null,
                status = AttendanceStatus.LATE,
                notes = "Traffic delay on Ring Road"
            ),
            AttendanceRecord(
                id = "att-3",
                staffId = "staff-4",
                staffName = "Nour El-Din",
                date = todayStr,
                checkInTime = "08:30 AM",
                checkOutTime = null,
                status = AttendanceStatus.PRESENT
            ),
            AttendanceRecord(
                id = "att-4",
                staffId = "staff-5",
                staffName = "Rania Sameh",
                date = todayStr,
                checkInTime = "08:50 AM",
                checkOutTime = null,
                status = AttendanceStatus.PRESENT
            ),
            AttendanceRecord(
                id = "att-5",
                staffId = "staff-6",
                staffName = "Hany Youssef",
                date = todayStr,
                checkInTime = null,
                checkOutTime = null,
                status = AttendanceStatus.ON_LEAVE,
                notes = "Annual leave approved"
            )
        )

        _staff.value = sampleStaff
        _attendance.value = sampleAttendance
    }

    override fun getStaff(): Flow<List<StaffMember>> = _staff.asStateFlow()

    override fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>> {
        return _attendance.map { list -> list.filter { it.date == date } }
    }

    override suspend fun logAttendance(record: AttendanceRecord): Result<AttendanceRecord> {
        val current = _attendance.value
        val index = current.indexOfFirst { it.staffId == record.staffId && it.date == record.date }
        val updated = current.toMutableList()
        if (index != -1) {
            updated[index] = record
        } else {
            updated.add(0, record)
        }
        _attendance.value = updated
        return Result.success(record)
    }

    override suspend fun addStaff(member: StaffMember): Result<StaffMember> {
        _staff.value = listOf(member) + _staff.value
        return Result.success(member)
    }

    override suspend fun deleteStaff(staffId: String): Result<Unit> {
        _staff.value = _staff.value.filter { it.id != staffId }
        return Result.success(Unit)
    }
}
