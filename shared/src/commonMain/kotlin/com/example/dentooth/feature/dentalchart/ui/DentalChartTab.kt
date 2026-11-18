package com.example.dentooth.feature.dentalchart.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dentooth.core.designsystem.components.DentoothLoadingState
import com.example.dentooth.core.designsystem.components.DentoothStatusBadge
import com.example.dentooth.feature.dentalchart.domain.model.ToothStatus
import com.example.dentooth.feature.dentalchart.ui.components.DentalArchView
import com.example.dentooth.feature.dentalchart.ui.components.ToothDetailSheet

@Composable
fun DentalChartTab(
    viewModel: DentalChartViewModel,
    onPlanProcedure: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val selectedTooth by viewModel.selectedTooth.collectAsState()

    if (state.isLoading || state.chart == null) {
        DentoothLoadingState(message = "Loading dental chart...")
    } else {
        val chart = state.chart!!

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Arch Controls Header (Adult / Pediatric Toggle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (chart.isPediatric) "Pediatric Chart (Deciduous)" else "Adult Dental Chart (Permanent)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "FDI Notation • Tap tooth to chart surfaces",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = !chart.isPediatric,
                        onClick = { viewModel.togglePediatricMode(false) },
                        label = { Text("Adult") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    FilterChip(
                        selected = chart.isPediatric,
                        onClick = { viewModel.togglePediatricMode(true) },
                        label = { Text("Pediatric") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            // Interactive Dental Arch Canvas Component
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                DentalArchView(
                    dentalChart = chart,
                    selectedToothNumber = selectedTooth?.toothNumber,
                    onToothSelected = { viewModel.selectTooth(it) }
                )
            }

            // Summary Statistics of Clinical Conditions
            val cariesCount = chart.teeth.values.count { it.status == ToothStatus.CARIES }
            val filledCount = chart.teeth.values.count { it.status == ToothStatus.FILLED }
            val crownCount = chart.teeth.values.count { it.status == ToothStatus.CROWN }
            val rctCount = chart.teeth.values.count { it.status == ToothStatus.ROOT_CANAL }
            val missingCount = chart.teeth.values.count { it.status == ToothStatus.MISSING }
            val implantCount = chart.teeth.values.count { it.status == ToothStatus.IMPLANT }

            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Clinical Findings Summary",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (cariesCount > 0) DentoothStatusBadge(text = "$cariesCount Caries", backgroundColor = ToothStatus.CARIES.color)
                        if (filledCount > 0) DentoothStatusBadge(text = "$filledCount Restored", backgroundColor = ToothStatus.FILLED.color)
                        if (crownCount > 0) DentoothStatusBadge(text = "$crownCount Crowns", backgroundColor = ToothStatus.CROWN.color)
                        if (rctCount > 0) DentoothStatusBadge(text = "$rctCount Endodontic", backgroundColor = ToothStatus.ROOT_CANAL.color)
                        if (missingCount > 0) DentoothStatusBadge(text = "$missingCount Missing", backgroundColor = ToothStatus.MISSING.color)
                        if (implantCount > 0) DentoothStatusBadge(text = "$implantCount Implants", backgroundColor = ToothStatus.IMPLANT.color)
                        if (cariesCount == 0 && filledCount == 0 && crownCount == 0 && rctCount == 0 && missingCount == 0) {
                            Text(
                                text = "All teeth recorded in healthy condition.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Tooth Detail Bottom Sheet
        if (selectedTooth != null) {
            ToothDetailSheet(
                tooth = selectedTooth!!,
                onDismiss = { viewModel.clearSelectedTooth() },
                onStatusChange = { viewModel.updateToothStatus(it) },
                onSurfaceStatusChange = { surf, st -> viewModel.updateSurfaceStatus(surf, st) },
                onAddCondition = { viewModel.addToothCondition(it) },
                onPlanProcedure = { toothNum -> onPlanProcedure(toothNum) }
            )
        }
    }
}
