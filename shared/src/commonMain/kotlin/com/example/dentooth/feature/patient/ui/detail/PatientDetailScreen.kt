package com.example.dentooth.feature.patient.ui.detail

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.core.designsystem.components.DentoothAvatar
import com.example.dentooth.core.designsystem.components.DentoothConfirmDialog
import com.example.dentooth.core.designsystem.components.DentoothErrorState
import com.example.dentooth.core.designsystem.components.DentoothInfoRow
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothStatusBadge
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.dentalchart.ui.DentalChartTab
import com.example.dentooth.feature.dentalchart.ui.DentalChartViewModel
import com.example.dentooth.feature.patient.domain.model.Patient
import com.example.dentooth.feature.patient.ui.form.PatientFormScreen
import com.example.dentooth.feature.treatment.ui.TreatmentPlansTab
import com.example.dentooth.feature.treatment.ui.TreatmentViewModel
import org.koin.core.parameter.parametersOf

data class PatientDetailScreen(val patientId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val patientViewModel = koinScreenModel<PatientDetailViewModel> { parametersOf(patientId) }
        val dentalChartViewModel = koinScreenModel<DentalChartViewModel> { parametersOf(patientId) }
        val treatmentViewModel = koinScreenModel<TreatmentViewModel> { parametersOf(patientId) }

        val state by patientViewModel.uiState.collectAsState()
        var showDeleteDialog by remember { mutableStateOf(false) }
        var selectedToothForProcedure by remember { mutableStateOf<Int?>(null) }

        if (showDeleteDialog) {
            DentoothConfirmDialog(
                title = "Delete Patient Record",
                message = "Are you sure you want to delete this patient? This action cannot be undone.",
                confirmText = "Delete",
                isDestructive = true,
                onConfirm = {
                    showDeleteDialog = false
                    patientViewModel.deletePatient { navigator.pop() }
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        Scaffold(
            topBar = {
                DentoothTopBar(
                    title = "Patient Profile",
                    onBackClick = { navigator.pop() },
                    actions = {
                        IconButton(onClick = { navigator.push(PatientFormScreen(patientId)) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            val patient = state.patientState.dataOrNull()

            if (state.patientState.isLoading) {
                DentoothLoadingState(message = "Loading patient profile...")
            } else if (patient == null) {
                DentoothErrorState(
                    message = "Could not load patient details",
                    onRetry = { patientViewModel.loadData() }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Patient Header Card
                    PatientHeaderSection(patient = patient)

                    // Navigation Tabs: Overview, Dental Chart, Treatment Plans, Medical History, Notes
                    PrimaryTabRow(
                        selectedTabIndex = state.selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        Tab(
                            selected = state.selectedTab == 0,
                            onClick = { patientViewModel.selectTab(0) },
                            text = { Text("Overview") }
                        )
                        Tab(
                            selected = state.selectedTab == 1,
                            onClick = { patientViewModel.selectTab(1) },
                            text = { Text("🦷 Chart") }
                        )
                        Tab(
                            selected = state.selectedTab == 2,
                            onClick = { patientViewModel.selectTab(2) },
                            text = { Text("📋 Plans") }
                        )
                        Tab(
                            selected = state.selectedTab == 3,
                            onClick = { patientViewModel.selectTab(3) },
                            text = { Text("🩺 History") }
                        )
                        Tab(
                            selected = state.selectedTab == 4,
                            onClick = { patientViewModel.selectTab(4) },
                            text = { Text("📝 Notes") }
                        )
                    }

                    // Tab Content
                    when (state.selectedTab) {
                        0 -> OverviewTab(patient)
                        1 -> DentalChartTab(
                            viewModel = dentalChartViewModel,
                            onPlanProcedure = { toothNum ->
                                selectedToothForProcedure = toothNum
                                patientViewModel.selectTab(2) // Jump to Treatment Plans
                            }
                        )
                        2 -> TreatmentPlansTab(
                            viewModel = treatmentViewModel,
                            initialToothNumberForNewProcedure = selectedToothForProcedure
                        )
                        3 -> {
                            val medHistory = state.medicalHistoryState.dataOrNull()
                            if (medHistory != null) {
                                MedicalHistoryTab(
                                    medicalHistory = medHistory,
                                    isSaving = state.isSavingMedicalHistory,
                                    onSave = { patientViewModel.updateMedicalHistory(it) }
                                )
                            } else {
                                DentoothLoadingState(message = "Loading medical history...")
                            }
                        }
                        4 -> PatientNotesTab(
                            patient = patient,
                            onSaveNotes = { notes -> }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun PatientHeaderSection(patient: Patient) {
        val age = patient.dateOfBirth?.let { DateTimeUtil.calculateAge(it) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DentoothAvatar(
                        name = patient.fullName,
                        size = 56.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = patient.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            DentoothStatusBadge(
                                text = patient.patientNumber,
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${patient.gender.displayName}${if (age != null) " • $age years" else ""} • ${patient.phone}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun OverviewTab(patient: Patient) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Financial Status Banner
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = if (patient.balanceDue > 0)
                        DentoothColors.InvoicePartial.copy(alpha = 0.1f)
                    else
                        DentoothColors.InvoicePaid.copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Account Balance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (patient.balanceDue > 0) "EGP ${patient.balanceDue.toInt()} Due" else "Fully Paid",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (patient.balanceDue > 0) DentoothColors.InvoicePartial else DentoothColors.InvoicePaid
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Billed: EGP ${patient.totalBilled.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Total Paid: EGP ${patient.totalPaid.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Contact & Demographic Details
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Contact & Profile Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    DentoothInfoRow(label = "Phone", value = patient.phone)
                    if (patient.phoneSecondary != null) {
                        DentoothInfoRow(label = "Secondary Phone", value = patient.phoneSecondary)
                    }
                    if (patient.email != null) {
                        DentoothInfoRow(label = "Email", value = patient.email)
                    }
                    if (patient.address != null) {
                        DentoothInfoRow(label = "Address", value = patient.address)
                    }
                    if (patient.lastVisitDate != null) {
                        DentoothInfoRow(label = "Last Visit", value = DateTimeUtil.formatDate(patient.lastVisitDate))
                    }
                }
            }
        }
    }
}
