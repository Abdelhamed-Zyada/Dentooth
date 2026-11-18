package com.example.dentooth.feature.treatment.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.dentooth.core.designsystem.components.DentoothEmptyState
import com.example.dentooth.core.designsystem.components.DentoothFormField
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothStatusBadge
import com.example.dentooth.feature.treatment.domain.model.PlanProcedure
import com.example.dentooth.feature.treatment.domain.model.PlanStatus
import com.example.dentooth.feature.treatment.domain.model.ProcedureStatus
import com.example.dentooth.feature.treatment.domain.model.TreatmentPlan

@Composable
fun TreatmentPlansTab(
    viewModel: TreatmentViewModel,
    initialToothNumberForNewProcedure: Int? = null,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var showNewPlanDialog by remember { mutableStateOf(false) }
    var activePlanIdForAddingProcedure by remember(initialToothNumberForNewProcedure) {
        mutableStateOf(if (initialToothNumberForNewProcedure != null && state.plans.isNotEmpty()) state.plans.first().id else null)
    }

    if (showNewPlanDialog) {
        NewPlanDialog(
            onDismiss = { showNewPlanDialog = false },
            onConfirm = { title ->
                showNewPlanDialog = false
                viewModel.createPlan(title)
            }
        )
    }

    if (activePlanIdForAddingProcedure != null) {
        ProcedureSelectorDialog(
            catalog = state.catalog,
            initialToothNumber = initialToothNumberForNewProcedure,
            onDismiss = { activePlanIdForAddingProcedure = null },
            onProcedureSelected = { item, toothNum, cost ->
                val planId = activePlanIdForAddingProcedure!!
                activePlanIdForAddingProcedure = null
                viewModel.addProcedure(planId, item, toothNum, cost)
            }
        )
    }

    if (state.isLoading) {
        DentoothLoadingState(message = "Loading treatment plans...")
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with "+ New Plan" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Treatment Plans (${state.plans.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Clinical sequencing and procedure tracking",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showNewPlanDialog = true },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Plan")
                }
            }

            if (state.plans.isEmpty()) {
                DentoothEmptyState(
                    title = "No Treatment Plans",
                    message = "This patient doesn't have an active or proposed treatment plan yet.",
                    actionLabel = "Create Treatment Plan",
                    onAction = { showNewPlanDialog = true }
                )
            } else {
                state.plans.forEach { plan ->
                    TreatmentPlanCard(
                        plan = plan,
                        onAddProcedureClick = { activePlanIdForAddingProcedure = plan.id },
                        onUpdateProcedureStatus = { procId, st -> viewModel.updateProcedureStatus(plan.id, procId, st) },
                        onUpdatePlanStatus = { st -> viewModel.updatePlanStatus(plan.id, st) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TreatmentPlanCard(
    plan: TreatmentPlan,
    onAddProcedureClick: () -> Unit,
    onUpdateProcedureStatus: (String, ProcedureStatus) -> Unit,
    onUpdatePlanStatus: (PlanStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Plan Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = plan.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Doctor: ${plan.doctorName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DentoothStatusBadge(
                    text = plan.status.displayName,
                    backgroundColor = plan.status.color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Text(
                text = "Progress: ${plan.completedProceduresCount} of ${plan.procedures.size} procedures completed",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { plan.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Financial Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estimated Total:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "EGP ${plan.totalCost.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Procedure Items List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                plan.procedures.forEach { proc ->
                    ProcedureItemRow(
                        procedure = proc,
                        onStatusChange = { newStatus -> onUpdateProcedureStatus(proc.id, newStatus) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onAddProcedureClick,
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Procedure")
                }

                if (plan.status != PlanStatus.COMPLETED && plan.procedures.isNotEmpty() && plan.completedProceduresCount == plan.procedures.size) {
                    Button(
                        onClick = { onUpdatePlanStatus(PlanStatus.COMPLETED) },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Mark Plan Complete")
                    }
                }
            }
        }
    }
}

@Composable
private fun ProcedureItemRow(
    procedure: PlanProcedure,
    onStatusChange: (ProcedureStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (procedure.toothNumber != null) {
                        DentoothStatusBadge(
                            text = "#${procedure.toothNumber}",
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            textColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = procedure.procedureName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "EGP ${procedure.totalCost.toInt()} • ${procedure.status.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status toggle action
            when (procedure.status) {
                ProcedureStatus.PLANNED -> {
                    TextButton(onClick = { onStatusChange(ProcedureStatus.IN_PROGRESS) }) {
                        Text("Start", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                ProcedureStatus.IN_PROGRESS -> {
                    Button(onClick = { onStatusChange(ProcedureStatus.COMPLETED) }) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Done")
                    }
                }
                ProcedureStatus.COMPLETED -> {
                    DentoothStatusBadge(
                        text = "Completed",
                        backgroundColor = MaterialTheme.colorScheme.secondary
                    )
                }
                ProcedureStatus.CANCELLED -> {
                    DentoothStatusBadge(
                        text = "Cancelled",
                        backgroundColor = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun NewPlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Treatment Plan", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Enter a title for this treatment plan (e.g. 'Comprehensive Rehabilitation', 'Ortho Phase 1'):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                DentoothFormField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Plan Title",
                    placeholder = "e.g. Full Mouth Restoration"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title) },
                enabled = title.isNotBlank()
            ) {
                Text("Create Plan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
