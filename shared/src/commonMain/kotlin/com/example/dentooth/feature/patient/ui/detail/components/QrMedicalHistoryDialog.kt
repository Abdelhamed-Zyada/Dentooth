package com.example.dentooth.feature.patient.ui.detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.patient.domain.model.MedicalHistory
import com.example.dentooth.feature.patient.domain.model.Patient

@Composable
fun QrMedicalHistoryDialog(
    patient: Patient,
    currentHistory: MedicalHistory?,
    onDismiss: () -> Unit,
    onSubmitNewHistory: (MedicalHistory) -> Unit
) {
    var showIntakeFormSimulator by remember { mutableStateOf(false) }

    if (showIntakeFormSimulator) {
        PatientMobileIntakeSheet(
            patient = patient,
            initialHistory = currentHistory,
            onDismiss = { showIntakeFormSimulator = false },
            onSubmit = { updated ->
                onSubmitNewHistory(updated)
                showIntakeFormSimulator = false
                onDismiss()
            }
        )
        return
    }

    val intakeUrl = "https://dentobrain.app/intake?token=${patient.id}-2026-auth"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Patient QR Intake Link",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Patient scans with phone camera to fill their medical history questionnaire privately before treatment.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Canvas QR Code Visual Pattern
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .size(190.dp)
                        .padding(8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(174.dp)) {
                        val gridSize = 19
                        val cellSize = size.width / gridSize
                        val seed = (patient.id.hashCode() and 0x7FFFFFFF) + 42

                        for (r in 0 until gridSize) {
                            for (c in 0 until gridSize) {
                                val isCornerBox = (r < 7 && c < 7) || (r < 7 && c >= gridSize - 7) || (r >= gridSize - 7 && c < 7)
                                val isFill = if (isCornerBox) {
                                    (r == 0 || r == 6 || c == 0 || c == 6) || (r in 2..4 && c in 2..4) ||
                                    (r == 0 || r == 6 || c == gridSize - 1 || c == gridSize - 7) || (r in 2..4 && c in gridSize - 5..gridSize - 3) ||
                                    (r == gridSize - 1 || r == gridSize - 7 || c == 0 || c == 6) || (r in gridSize - 5..gridSize - 3 && c in 2..4)
                                } else {
                                    val hash = (r * 31 + c * 17 + seed) % 7
                                    hash % 2 == 0
                                }

                                if (isFill) {
                                    drawRect(
                                        color = Color.Black,
                                        topLeft = Offset(c * cellSize, r * cellSize),
                                        size = Size(cellSize - 0.5f, cellSize - 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Patient: ${patient.fullName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Secure Token: ${patient.id}-2026-auth • Expires in 24h",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Share Link or Test Simulated Scan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Link")
                    }

                    Button(
                        onClick = { showIntakeFormSimulator = true },
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Text("Simulate Patient Scan")
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientMobileIntakeSheet(
    patient: Patient,
    initialHistory: MedicalHistory?,
    onDismiss: () -> Unit,
    onSubmit: (MedicalHistory) -> Unit
) {
    var hasDiabetes by remember { mutableStateOf(initialHistory?.hasDiabetes ?: false) }
    var hasHypertension by remember { mutableStateOf(initialHistory?.hasHypertension ?: false) }
    var hasHeartDisease by remember { mutableStateOf(initialHistory?.hasHeartDisease ?: false) }
    var hasBleedingDisorders by remember { mutableStateOf(initialHistory?.hasBleedingDisorder ?: false) }
    var hasAllergies by remember { mutableStateOf(!initialHistory?.allergies.isNullOrBlank()) }
    var allergiesDetails by remember { mutableStateOf(initialHistory?.allergies ?: "") }
    var isPregnant by remember { mutableStateOf(initialHistory?.isPregnant ?: false) }
    var currentMedications by remember { mutableStateOf(initialHistory?.currentMedications ?: "") }
    var otherConditions by remember { mutableStateOf(initialHistory?.otherConditions ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Mobile Web View Header Simulation
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌐", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "DentoBrain Mobile Patient Portal",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Welcome ${patient.fullName} — Medical Intake Questionnaire",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Please answer honestly for your clinical safety:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(10.dp))

                CheckboxItem("Diabetes (السكري)", hasDiabetes) { hasDiabetes = it }
                CheckboxItem("Hypertension / High Blood Pressure (ضغط الدم)", hasHypertension) { hasHypertension = it }
                CheckboxItem("Cardiovascular / Heart Disease (أمراض القلب)", hasHeartDisease) { hasHeartDisease = it }
                CheckboxItem("Bleeding Tendency / Anticoagulant use (سيولة الدم)", hasBleedingDisorders) { hasBleedingDisorders = it }

                CheckboxItem("Drug / Penicillin Allergy (حساسية الأدوية أو البنسلين)", hasAllergies) { hasAllergies = it }
                if (hasAllergies) {
                    OutlinedTextField(
                        value = allergiesDetails,
                        onValueChange = { allergiesDetails = it },
                        label = { Text("List Allergies") },
                        placeholder = { Text("e.g. Penicillin, Sulfa, Aspirin") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, bottom = 8.dp)
                    )
                }

                if (patient.gender.name == "FEMALE") {
                    CheckboxItem("Current Pregnancy (حمل حالي)", isPregnant) { isPregnant = it }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = currentMedications,
                    onValueChange = { currentMedications = it },
                    label = { Text("Current Daily Medications (الأدوية الحالية)") },
                    placeholder = { Text("e.g. Concor 5mg, Glucophage 1000mg") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = otherConditions,
                    onValueChange = { otherConditions = it },
                    label = { Text("Any Other Surgeries or Medical History") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val base = initialHistory ?: MedicalHistory(
                                id = "med-${DateTimeUtil.currentInstant().toEpochMilliseconds()}",
                                patientId = patient.id
                            )
                            val updated = base.copy(
                                hasDiabetes = hasDiabetes,
                                hasHypertension = hasHypertension,
                                hasHeartDisease = hasHeartDisease,
                                hasBleedingDisorder = hasBleedingDisorders,
                                allergies = if (hasAllergies) allergiesDetails.trim() else "",
                                isPregnant = isPregnant,
                                currentMedications = currentMedications.trim(),
                                otherConditions = otherConditions.trim(),
                                lastUpdated = DateTimeUtil.today().toString()
                            )
                            onSubmit(updated)
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Submit", modifier = Modifier.padding(end = 4.dp))
                        Text("Sign & Submit Questionnaire")
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckboxItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
