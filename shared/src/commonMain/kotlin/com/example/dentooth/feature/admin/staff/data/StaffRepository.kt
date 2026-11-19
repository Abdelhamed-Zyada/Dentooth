package com.example.dentooth.feature.admin.staff.data

import com.example.dentooth.feature.admin.staff.domain.model.AttendanceRecord
import com.example.dentooth.feature.admin.staff.domain.model.StaffMember
import kotlinx.coroutines.flow.Flow

interface StaffRepository {
    fun getStaff(): Flow<List<StaffMember>>
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>>
    suspend fun logAttendance(record: AttendanceRecord): Result<AttendanceRecord>
    suspend fun addStaff(member: StaffMember): Result<StaffMember>
    suspend fun deleteStaff(staffId: String): Result<Unit>
}
