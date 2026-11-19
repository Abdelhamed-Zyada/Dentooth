package com.example.dentooth.feature.dentalchart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.core.designsystem.components.DentoothFormField
import com.example.dentooth.core.designsystem.components.DentoothStatusBadge
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.dentalchart.domain.model.ToothCondition
import com.example.dentooth.feature.dentalchart.domain.model.ToothRecord
import com.example.dentooth.feature.dentalchart.domain.model.ToothStatus
import com.example.dentooth.feature.dentalchart.domain.model.ToothSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToothDetailSheet(
    tooth: ToothRecord,
    onDismiss: () -> Unit,
    onStatusChange: (ToothStatus) -> Unit,
    onSurfaceStatusChange: (ToothSurface, ToothStatus) -> Unit,
    onAddCondition: (ToothCondition) -> Unit,
    onPlanProcedure: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSurface by remember { mutableStateOf<ToothSurface?>(null) }
    var showConditionDialog by remember { mutableStateOf(false) }

    if (showConditionDialog) {
        AddConditionDialog(
            toothNumber = tooth.toothNumber,
            onDismiss = { showConditionDialog = false },
            onConfirm = { condition ->
                showConditionDialog = false
                onAddCondition(condition)
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Tooth Number and Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${tooth.toothNumber}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DentoothStatusBadge(
                            text = tooth.status.displayName,
                            backgroundColor = tooth.status.color,
                            textColor = if (tooth.status == ToothStatus.HEALTHY) Color.Black else Color.White
                        )
                    }
                    Text(
                        text = tooth.toothName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            // 1. Overall Status Quick-Selector Chips
            Text(
                text = "Overall Clinical Status",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    ToothStatus.HEALTHY,
                    ToothStatus.CARIES,
                    ToothStatus.FILLED,
                    ToothStatus.CROWN,
                    ToothStatus.ROOT_CANAL,
                    ToothStatus.IMPLANT,
                    ToothStatus.MISSING
                ).forEach { status ->
                    FilterChip(
                        selected = tooth.status == status,
                        onClick = { onStatusChange(status) },
                        label = { Text(status.displayName) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(status.color)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            // 2. Individual Surface Charting
            Text(
                text = "Surface-Level Charting",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Select a surface to assign caries, composite, or restore to healthy:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ToothSurface.entries.forEach { surface ->
                            val surfStatus = tooth.surfaceStatuses[surface] ?: ToothStatus.HEALTHY
                            SurfaceChip(
                                surface = surface,
                                status = surfStatus,
                                isSelected = selectedSurface == surface,
                                onClick = {
                                    selectedSurface = if (selectedSurface == surface) null else surface
                                }
                            )
                        }
                    }

                    if (selectedSurface != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Set ${selectedSurface!!.displayName} (${selectedSurface!!.code}) to:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onSurfaceStatusChange(selectedSurface!!, ToothStatus.CARIES)
                                    selectedSurface = null
                                }
                            ) {
                                Text("Caries")
                            }
                            Button(
                                onClick = {
                                    onSurfaceStatusChange(selectedSurface!!, ToothStatus.FILLED)
                                    selectedSurface = null
                                }
                            ) {
                                Text("Filled")
                            }
                            OutlinedButton(
                                onClick = {
                                    onSurfaceStatusChange(selectedSurface!!, ToothStatus.HEALTHY)
                                    selectedSurface = null
                                }
                            ) {
                                Text("Healthy")
                            }
                        }
                    }
                }
            }

            // 3. Clinical Conditions on this Tooth
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diagnosed Conditions (${tooth.conditions.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "+ Add",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showConditionDialog = true }
                )
            }

            if (tooth.conditions.isEmpty()) {
                Text(
                    text = "No pathological conditions recorded on this tooth.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    tooth.conditions.forEach { condition ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = condition.type,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (condition.surfaces.isNotEmpty()) {
                                        Text(
                                            text = "Surfaces: ${condition.surfaces.joinToString { it.code }}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                DentoothStatusBadge(
                                    text = condition.severity,
                                    backgroundColor = DentoothColors.Tertiary
                                )
                            }
                        }
                    }
                }
            }

            // 4. Action Button: Plan Treatment Procedure for this Tooth
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onDismiss()
                    onPlanProcedure(tooth.toothNumber)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Plan Procedure for #${tooth.toothNumber}")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SurfaceChip(
    surface: ToothSurface,
    status: ToothStatus,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(status.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = surface.code,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (status == ToothStatus.HEALTHY) Color.Black else Color.White
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = surface.displayName.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp
        )
    }
}

@Composable
fun AddConditionDialog(
    toothNumber: Int,
    onDismiss: () -> Unit,
    onConfirm: (ToothCondition) -> Unit
) {
    val commonConditions = listOf(
        "Deep Dentinal Caries",
        "Enamel Caries",
        "Enamel Fracture",
        "Cusp Fracture",
        "Periapical Abscess / Lesion",
        "Irreversible Pulpitis",
        "Mobility Grade 1",
        "Severe Attrition / Bruxism",
        "Cervical Abrasion"
    )

    var selectedType by remember { mutableStateOf(commonConditions.first()) }
    var severity by remember { mutableStateOf("Moderate") }
    val selectedSurfaces = remember { mutableStateOf(setOf<ToothSurface>()) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Condition to #$toothNumber",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Condition Type:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    commonConditions.take(5).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                Text(
                    text = "Involved Surfaces:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ToothSurface.entries.forEach { surf ->
                        val isChecked = selectedSurfaces.value.contains(surf)
                        FilterChip(
                            selected = isChecked,
                            onClick = {
                                selectedSurfaces.value = if (isChecked) {
                                    selectedSurfaces.value - surf
                                } else {
                                    selectedSurfaces.value + surf
                                }
                            },
                            label = { Text(surf.code) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                Text(
                    text = "Severity:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Mild", "Moderate", "Severe").forEach { sev ->
                        FilterChip(
                            selected = severity == sev,
                            onClick = { severity = sev },
                            label = { Text(sev) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }

                DentoothFormField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Clinical Notes",
                    placeholder = "Observations or thermal test response"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val condition = ToothCondition(
                        id = "cond-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                        toothNumber = toothNumber,
                        type = selectedType,
                        surfaces = selectedSurfaces.value.toList(),
                        severity = severity,
                        notes = notes.trim().ifEmpty { null },
                        diagnosedAt = DateTimeUtil.formatDate(DateTimeUtil.today())
                    )
                    onConfirm(condition)
                }
            ) {
                Text("Save Condition", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
