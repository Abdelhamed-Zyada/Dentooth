package com.example.dentooth.feature.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.core.designsystem.components.DentoBrainLogo
import com.example.dentooth.core.designsystem.components.DentoothAvatar
import com.example.dentooth.core.designsystem.components.DentoothStatCard
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.core.localization.appStrings
import com.example.dentooth.core.localization.ui.LanguageToggleButton
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.admin.analytics.ui.AnalyticsScreen
import com.example.dentooth.feature.admin.branch.ui.BranchSelectorSheet
import com.example.dentooth.feature.admin.inventory.ui.InventoryScreen
import com.example.dentooth.feature.admin.settings.ui.ClinicSettingsScreen
import com.example.dentooth.feature.admin.staff.ui.StaffScreen
import com.example.dentooth.feature.automation.ui.AutomationScreen
import com.example.dentooth.feature.calendar.domain.model.Appointment
import com.example.dentooth.feature.insurance.ui.InsuranceScreen
import com.example.dentooth.feature.lab.ui.LabScreen
import com.example.dentooth.feature.patient.ui.form.PatientFormScreen

class DashboardScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<DashboardViewModel>()
        val state by viewModel.state.collectAsState()
        val s = appStrings

        var showBranchSelector by remember { mutableStateOf(false) }

        val userName = state.userSession?.fullName ?: s.doctor
        val todayStr = DateTimeUtil.formatDate(DateTimeUtil.today())
        val activeBranchName = state.activeBranch?.name ?: s.mainClinic

        Scaffold(
            topBar = {
                DentoothTopBar(
                    title = s.appName,
                    navigationIcon = {
                        DentoBrainLogo(
                            size = 36.dp,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        )
                    },
                    actions = {
                        LanguageToggleButton()
                        IconButton(onClick = { navigator.push(ClinicSettingsScreen()) }) {
                            Icon(Icons.Default.Settings, contentDescription = s.tileSettings)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Greeting & Active Branch Switcher Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = s.goodMorning,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Branch Switcher Pill
                        Surface(
                            onClick = { showBranchSelector = true },
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = s.branchSelector,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$activeBranchName ▾",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    DentoothAvatar(name = userName, size = 52.dp)
                }

                // Primary Quick Actions (Patient + Appt)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { navigator.push(PatientFormScreen()) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(s.btnNewPatient)
                    }
                    OutlinedButton(
                        onClick = { navigator.push(PatientFormScreen()) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(s.btnAddIntake)
                    }
                }

                // Administration Module Shortcuts Row
                Text(
                    text = s.clinicAdminSection,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminQuickTile(
                        icon = "📦",
                        title = s.tileInventory,
                        subtitle = "${state.lowStockItemsCount} ${s.lowStock}",
                        onClick = { navigator.push(InventoryScreen()) }
                    )

                    AdminQuickTile(
                        icon = "👥",
                        title = s.tileStaff,
                        subtitle = s.tileStaffSubtitle,
                        onClick = { navigator.push(StaffScreen()) }
                    )

                    AdminQuickTile(
                        icon = "📊",
                        title = s.tileAnalytics,
                        subtitle = s.tileAnalyticsSubtitle,
                        onClick = { navigator.push(AnalyticsScreen()) }
                    )

                    AdminQuickTile(
                        icon = "⚙️",
                        title = s.tileSettings,
                        subtitle = s.tileSettingsSubtitle,
                        onClick = { navigator.push(ClinicSettingsScreen()) }
                    )
                }

                // Operations & Patient Engagement Shortcuts Row
                Text(
                    text = s.operationsEngagementSection,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminQuickTile(
                        icon = "🧪",
                        title = s.tileLabs,
                        subtitle = s.tileLabsSubtitle,
                        onClick = { navigator.push(LabScreen()) }
                    )

                    AdminQuickTile(
                        icon = "🛡️",
                        title = s.tileInsurance,
                        subtitle = s.tileInsuranceSubtitle,
                        onClick = { navigator.push(InsuranceScreen()) }
                    )

                    AdminQuickTile(
                        icon = "⚡",
                        title = s.tileAutomations,
                        subtitle = s.tileAutomationsSubtitle,
                        onClick = { navigator.push(AutomationScreen()) }
                    )
                }

                // KPI Stat Cards Grid (2x2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DentoothStatCard(
                        title = s.todayAppointmentsCount,
                        value = "${state.todayAppointmentsCount}",
                        icon = Icons.Default.DateRange,
                        iconTint = DentoothColors.StatusScheduled,
                        modifier = Modifier.weight(1f)
                    )
                    DentoothStatCard(
                        title = s.todayRevenue,
                        value = "${s.currency} ${formatNumber(state.todayRevenue)}",
                        icon = Icons.Default.ShoppingCart,
                        iconTint = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DentoothStatCard(
                        title = s.totalPatients,
                        value = "${state.totalPatients}",
                        icon = Icons.Default.Person,
                        iconTint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    DentoothStatCard(
                        title = s.pendingInvoicesCount,
                        value = "${state.pendingInvoicesCount}",
                        icon = Icons.Default.ShoppingCart,
                        iconTint = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Low Stock Alerts Banner (if items are low)
                if (state.lowStockItemsCount > 0) {
                    Card(
                        onClick = { navigator.push(InventoryScreen()) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB74D)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⚠️", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${s.inventoryAlertTitle} (${state.lowStockItemsCount} ${s.items})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBF360C)
                                )
                                Text(
                                    text = s.inventoryAlertSubtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }

                // Today's Live Agenda Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${s.todaySchedule} (${state.todayAppointments.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = todayStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                if (state.todayAppointments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = s.noAppointmentsToday,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.todayAppointments.forEach { appt ->
                            LiveAppointmentItemCard(appt = appt)
                        }
                    }
                }
            }

            // Branch Selector Modal Sheet
            if (showBranchSelector && state.activeBranch != null) {
                BranchSelectorSheet(
                    branches = state.allBranches,
                    activeBranch = state.activeBranch!!,
                    onDismiss = { showBranchSelector = false },
                    onSelectBranch = { branch ->
                        viewModel.switchActiveBranch(branch.id)
                    }
                )
            }
        }
    }

    @Composable
    private fun LiveAppointmentItemCard(appt: Appointment) {
        val s = appStrings
        val statusColor = appt.displayColor

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${appt.startTime} - ${appt.endTime}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${appt.chairName ?: s.generalChair}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = appt.patientName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${appt.doctorName} • ${appt.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = appt.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun AdminQuickTile(
        icon: String,
        title: String,
        subtitle: String,
        onClick: () -> Unit
    ) {
        ElevatedCard(
            onClick = onClick,
            modifier = Modifier.width(130.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }

    private fun formatNumber(amount: Double): String {
        val rounded = (amount * 100).toLong() / 100.0
        val parts = rounded.toString().split(".")
        val intPart = parts[0].reversed().chunked(3).joinToString(",").reversed()
        return intPart
    }
}
