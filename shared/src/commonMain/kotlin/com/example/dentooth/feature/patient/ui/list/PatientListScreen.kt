package com.example.dentooth.feature.patient.ui.list

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.core.designsystem.components.DentoothAvatar
import com.example.dentooth.core.designsystem.components.DentoothEmptyState
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothSearchBar
import com.example.dentooth.core.designsystem.components.DentoothStatusBadge
import com.example.dentooth.core.designsystem.components.DentoothTopBar
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.patient.domain.model.Patient
import com.example.dentooth.feature.patient.ui.detail.PatientDetailScreen
import com.example.dentooth.feature.patient.ui.form.PatientFormScreen
import com.example.dentooth.feature.patient.ui.list.components.ManageGroupsDialog

class PatientListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<PatientListViewModel>()
        val state by viewModel.state.collectAsState()
        var showManageGroupsDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                DentoothTopBar(
                    title = "Patients (${state.filteredPatients.size})",
                    actions = {
                        IconButton(onClick = { showManageGroupsDialog = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Manage Patient Groups")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(PatientFormScreen()) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Patient")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search Bar
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    DentoothSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChanged(it) }
                    )
                }

                // Group Filter Chips
                if (state.availableGroups.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.selectedGroup == null,
                            onClick = { viewModel.onGroupSelected(null) },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                        state.availableGroups.forEach { group ->
                            FilterChip(
                                selected = state.selectedGroup == group.name,
                                onClick = { viewModel.onGroupSelected(group.name) },
                                label = { Text(group.name) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                        FilterChip(
                            selected = false,
                            onClick = { showManageGroupsDialog = true },
                            label = { Text("+ Groups") }
                        )
                    }
                }

                // Patient List or Empty State
                if (state.isLoading) {
                    DentoothLoadingState(message = "Loading patients...")
                } else if (state.filteredPatients.isEmpty()) {
                    DentoothEmptyState(
                        title = "No patients found",
                        message = if (state.searchQuery.isNotBlank()) "No patients matching \"${state.searchQuery}\"" else "Your clinic has no registered patients yet.",
                        actionLabel = if (state.searchQuery.isBlank()) "Register First Patient" else null,
                        onAction = { navigator.push(PatientFormScreen()) }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.filteredPatients, key = { it.id }) { patient ->
                            PatientCard(
                                patient = patient,
                                onClick = { navigator.push(PatientDetailScreen(patient.id)) }
                            )
                        }
                    }
                }
            }
        }

        if (showManageGroupsDialog) {
            ManageGroupsDialog(
                groups = state.availableGroups,
                patients = state.patients,
                onDismiss = { showManageGroupsDialog = false },
                onCreateGroup = { name, colorHex, desc ->
                    viewModel.createGroup(name, colorHex, desc)
                },
                onDeleteGroup = { groupId ->
                    viewModel.deleteGroup(groupId)
                }
            )
        }
    }

    @Composable
    private fun PatientCard(
        patient: Patient,
        onClick: () -> Unit
    ) {
        val age = patient.dateOfBirth?.let { DateTimeUtil.calculateAge(it) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DentoothAvatar(
                    name = patient.fullName,
                    size = 52.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = patient.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        DentoothStatusBadge(
                            text = patient.patientNumber,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            textColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${patient.gender.displayName}${if (age != null) " • $age yrs" else ""} • ${patient.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (patient.groups.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            patient.groups.take(3).forEach { group ->
                                DentoothStatusBadge(
                                    text = group,
                                    backgroundColor = DentoothColors.Primary.copy(alpha = 0.15f),
                                    textColor = DentoothColors.Primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
