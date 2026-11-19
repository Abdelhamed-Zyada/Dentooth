package com.example.dentooth.feature.admin.staff.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.components.DentoothAvatar
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.admin.staff.domain.model.AttendanceRecord
import com.example.dentooth.feature.admin.staff.domain.model.AttendanceStatus
import com.example.dentooth.feature.admin.staff.domain.model.StaffMember
import com.example.dentooth.feature.admin.staff.domain.model.StaffRole
import com.example.dentooth.feature.admin.staff.ui.components.AddStaffDialog
import com.example.dentooth.feature.admin.staff.ui.components.LogAttendanceDialog

class StaffScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<StaffViewModel>()
        val state by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                Column {
                    DentoothTopBar(
                        title = "Staff & Attendance",
                        onBackClick = { navigator.pop() }
                    )

                    PrimaryTabRow(
                        selectedTabIndex = state.activeTab,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Tab(
                            selected = state.activeTab == 0,
                            onClick = { viewModel.setActiveTab(0) },
                            text = { Text("Staff Directory (${state.allStaff.size})", fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = state.activeTab == 1,
                            onClick = { viewModel.setActiveTab(1) },
                            text = { Text("Today's Attendance", fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openAddStaff() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Staff")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (state.staffList.isLoading) {
                    DentoothLoadingState(message = "Loading staff profiles...")
                } else {
                    val staff = state.staffList.dataOrNull() ?: emptyList()

                    if (state.activeTab == 0) {
                        // Tab 0: Staff Directory
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Search input
                            item(key = "staff-search") {
                                OutlinedTextField(
                                    value = state.searchQuery,
                                    onValueChange = { viewModel.setSearchQuery(it) },
                                    placeholder = { Text("Search by doctor or staff name...") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Role Filter Chips
                            item(key = "role-chips") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = state.selectedRoleFilter == null,
                                        onClick = { viewModel.setRoleFilter(null) },
                                        label = { Text("All Staff") }
                                    )

                                    StaffRole.entries.forEach { role ->
                                        FilterChip(
                                            selected = state.selectedRoleFilter == role,
                                            onClick = {
                                                viewModel.setRoleFilter(if (state.selectedRoleFilter == role) null else role)
                                            },
                                            label = { Text(role.label) }
                                        )
                                    }
                                }
                            }

                            items(staff, key = { it.id }) { member ->
                                StaffMemberCard(
                                    member = member,
                                    onLogAttendance = { viewModel.openLogAttendance(member) }
                                )
                            }
                        }
                    } else {
                        // Tab 1: Today's Attendance
                        AttendanceOverviewTab(
                            records = state.attendanceRecords,
                            allStaff = state.allStaff,
                            onLogAttendance = { member -> viewModel.openLogAttendance(member) }
                        )
                    }
                }
            }

            // Dialogs
            state.staffToLogAttendance?.let { member ->
                LogAttendanceDialog(
                    member = member,
                    onDismiss = { viewModel.closeLogAttendance() },
                    onLog = { checkIn, checkOut, status, notes ->
                        viewModel.logAttendance(member.id, member.fullName, checkIn, checkOut, status, notes)
                    }
                )
            }

            if (state.isAddStaffOpen) {
                AddStaffDialog(
                    onDismiss = { viewModel.closeAddStaff() },
                    onAdd = { fullName, role, spec, license, phone, email, salary ->
                        viewModel.addStaff(fullName, role, spec, license, phone, email, salary)
                    }
                )
            }
        }
    }
}

@Composable
private fun StaffMemberCard(
    member: StaffMember,
    onLogAttendance: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DentoothAvatar(name = member.fullName, size = 44.dp)
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = member.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = member.role.composeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = member.role.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = member.role.composeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (!member.specialization.isNullOrBlank()) {
                        Text(
                            text = member.specialization,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!member.licenseNumber.isNullOrBlank()) {
                        Text(
                            text = "License: ${member.licenseNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Phone: ${member.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Branch: ${member.branchName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                FilledTonalButton(
                    onClick = onLogAttendance,
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Log Attendance", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun AttendanceOverviewTab(
    records: List<AttendanceRecord>,
    allStaff: List<StaffMember>,
    onLogAttendance: (StaffMember) -> Unit
) {
    val presentCount = records.count { it.status == AttendanceStatus.PRESENT }
    val lateCount = records.count { it.status == AttendanceStatus.LATE }
    val leaveCount = records.count { it.status == AttendanceStatus.ON_LEAVE }
    val absentCount = allStaff.size - (presentCount + lateCount + leaveCount).coerceAtLeast(0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Attendance KPI Cards
        item(key = "attendance-kpis") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AttendanceKpiCard(
                    title = "Present",
                    count = presentCount,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                AttendanceKpiCard(
                    title = "Late",
                    count = lateCount,
                    color = Color(0xFFFB8C00),
                    modifier = Modifier.weight(1f)
                )
                AttendanceKpiCard(
                    title = "On Leave",
                    count = leaveCount,
                    color = Color(0xFF546E7A),
                    modifier = Modifier.weight(1f)
                )
                AttendanceKpiCard(
                    title = "Absent",
                    count = absentCount.coerceAtLeast(0),
                    color = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item(key = "attendance-date-header") {
            Text(
                text = "Attendance Roster • ${DateTimeUtil.formatDate(DateTimeUtil.today())}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(allStaff, key = { it.id }) { member ->
            val record = records.find { it.staffId == member.id }
            val status = record?.status ?: AttendanceStatus.ABSENT

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = member.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = member.role.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )

                        if (record?.checkInTime != null) {
                            Text(
                                text = "Clock In: ${record.checkInTime}${record.checkOutTime?.let { " • Clock Out: $it" } ?: ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = status.composeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = status.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = status.composeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        FilledTonalButton(
                            onClick = { onLogAttendance(member) },
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Edit", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceKpiCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
