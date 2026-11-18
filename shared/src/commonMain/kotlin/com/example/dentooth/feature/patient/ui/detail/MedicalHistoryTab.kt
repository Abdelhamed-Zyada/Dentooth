package com.example.dentooth.feature.patient.ui.detail

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dentooth.core.designsystem.components.DentoothFormField
import com.example.dentooth.feature.patient.domain.model.MedicalHistory

@Composable
fun MedicalHistoryTab(
    medicalHistory: MedicalHistory,
    isSaving: Boolean,
    onSave: (MedicalHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    var hasDiabetes by remember(medicalHistory) { mutableStateOf(medicalHistory.hasDiabetes) }
    var hasHypertension by remember(medicalHistory) { mutableStateOf(medicalHistory.hasHypertension) }
    var hasHeartDisease by remember(medicalHistory) { mutableStateOf(medicalHistory.hasHeartDisease) }
    var hasHepatitis by remember(medicalHistory) { mutableStateOf(medicalHistory.hasHepatitis) }
    var hasAsthma by remember(medicalHistory) { mutableStateOf(medicalHistory.hasAsthma) }
    var hasBleedingDisorder by remember(medicalHistory) { mutableStateOf(medicalHistory.hasBleedingDisorder) }
    var isSmoker by remember(medicalHistory) { mutableStateOf(medicalHistory.isSmoker) }
    var allergies by remember(medicalHistory) { mutableStateOf(medicalHistory.allergies) }
    var currentMedications by remember(medicalHistory) { mutableStateOf(medicalHistory.currentMedications) }
    var otherConditions by remember(medicalHistory) { mutableStateOf(medicalHistory.otherConditions) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Systemic Conditions & Risk Factors",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                ConditionCheckboxRow("Diabetes Mellitus", hasDiabetes) { hasDiabetes = it }
                ConditionCheckboxRow("Hypertension (High Blood Pressure)", hasHypertension) { hasHypertension = it }
                ConditionCheckboxRow("Heart Disease / Cardiac condition", hasHeartDisease) { hasHeartDisease = it }
                ConditionCheckboxRow("Hepatitis (B / C)", hasHepatitis) { hasHepatitis = it }
                ConditionCheckboxRow("Asthma / Respiratory issues", hasAsthma) { hasAsthma = it }
                ConditionCheckboxRow("Bleeding Disorder / Anticoagulants", hasBleedingDisorder) { hasBleedingDisorder = it }
                ConditionCheckboxRow("Active Smoker / Tobacco use", isSmoker) { isSmoker = it }
            }
        }

        Text(
            text = "Allergies & Current Medications",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        DentoothFormField(
            value = allergies,
            onValueChange = { allergies = it },
            label = "Known Allergies",
            placeholder = "e.g. Penicillin, Latex, Local Anesthetics (or None)",
            singleLine = false,
            minLines = 2
        )

        DentoothFormField(
            value = currentMedications,
            onValueChange = { currentMedications = it },
            label = "Current Medications",
            placeholder = "e.g. Metformin, Aspirin, Blood thinners",
            singleLine = false,
            minLines = 2
        )

        DentoothFormField(
            value = otherConditions,
            onValueChange = { otherConditions = it },
            label = "Other Relevant Medical Notes",
            placeholder = "Past surgeries, pregnancy status, hospitalizations",
            singleLine = false,
            minLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onSave(
                    medicalHistory.copy(
                        hasDiabetes = hasDiabetes,
                        hasHypertension = hasHypertension,
                        hasHeartDisease = hasHeartDisease,
                        hasHepatitis = hasHepatitis,
                        hasAsthma = hasAsthma,
                        hasBleedingDisorder = hasBleedingDisorder,
                        isSmoker = isSmoker,
                        allergies = allergies.trim(),
                        currentMedications = currentMedications.trim(),
                        otherConditions = otherConditions.trim()
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isSaving
        ) {
            Text(if (isSaving) "Saving..." else "Update Medical History")
        }
    }
}

@Composable
private fun ConditionCheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
