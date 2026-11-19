package com.example.dentooth.feature.calendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dentooth.feature.calendar.domain.model.AppointmentConflict
import com.example.dentooth.feature.calendar.domain.model.AppointmentType
import com.example.dentooth.feature.calendar.domain.model.Chair
import com.example.dentooth.feature.calendar.domain.model.StaffDoctor
import com.example.dentooth.feature.calendar.ui.AppointmentBookingPrefill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingSheet(
    prefill: AppointmentBookingPrefill?,
    chairs: List<Chair>,
    doctors: List<StaffDoctor>,
    onDismiss: () -> Unit,
    onConflictCheck: suspend (doctorId: String, chairId: String?, date: String, startTime: String, endTime: String) -> AppointmentConflict,
    onBook: (
        patientId: String,
        patientName: String,
        patientPhone: String,
        doctorId: String,
        doctorName: String,
        chairId: String?,
        chairName: String?,
        title: String,
        procedureName: String?,
        toothNumber: Int?,
        date: String,
        startTime: String,
        endTime: String,
        type: AppointmentType,
        notes: String?
    ) -> Unit
) {
    var patientName by remember { mutableStateOf(prefill?.patientName ?: "Ahmed Ali") }
    var patientPhone by remember { mutableStateOf("+201012345678") }
    var patientId by remember { mutableStateOf(prefill?.patientId ?: "1") }

    var selectedDoctor by remember {
        mutableStateOf(doctors.firstOrNull { it.id == prefill?.doctorId } ?: doctors.firstOrNull())
    }
    var doctorExpanded by remember { mutableStateOf(false) }

    var selectedChair by remember {
        mutableStateOf(chairs.firstOrNull { it.id == prefill?.chairId } ?: chairs.firstOrNull())
    }
    var chairExpanded by remember { mutableStateOf(false) }

    var dateStr by remember { mutableStateOf(prefill?.date ?: "2026-11-19") }
    var startTimeStr by remember { mutableStateOf(prefill?.startTime ?: "09:00") }
    var durationMinutes by remember { mutableIntStateOf(60) }

    var selectedType by remember { mutableStateOf(AppointmentType.TREATMENT) }
    var title by remember { mutableStateOf("Dental Treatment Session") }
    var procedureName by remember { mutableStateOf("Molar Composite Restoration") }
    var toothNumberText by remember { mutableStateOf("16") }
    var notes by remember { mutableStateOf("") }

    var conflictWarning by remember { mutableStateOf<String?>(null) }
    var isCheckingConflict by remember { mutableStateOf(false) }

    // Calculate end time
    val endTimeStr = remember(startTimeStr, durationMinutes) {
        calculateEndTime(startTimeStr, durationMinutes)
    }

    // Auto-check conflict whenever doctor, chair, date, or times change
    LaunchedEffect(selectedDoctor, selectedChair, dateStr, startTimeStr, endTimeStr) {
        val doc = selectedDoctor
        if (doc != null) {
            isCheckingConflict = true
            val conflict = onConflictCheck(doc.id, selectedChair?.id, dateStr, startTimeStr, endTimeStr)
            conflictWarning = if (conflict.hasConflict) conflict.message else null
            isCheckingConflict = false
        }
    }

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Book New Appointment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Patient Information
                Text("Patient Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = patientPhone,
                    onValueChange = { patientPhone = it },
                    label = { Text("Contact Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Doctor Selection
                Text("Doctor & Operatory Chair", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = doctorExpanded,
                    onExpandedChange = { doctorExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDoctor?.name ?: "Select Doctor",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Attending Doctor") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = doctorExpanded,
                        onDismissRequest = { doctorExpanded = false }
                    ) {
                        doctors.forEach { doc ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(doc.name, fontWeight = FontWeight.SemiBold)
                                        Text(doc.specialization, style = MaterialTheme.typography.labelSmall)
                                    }
                                },
                                onClick = {
                                    selectedDoctor = doc
                                    doctorExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chair Selection
                ExposedDropdownMenuBox(
                    expanded = chairExpanded,
                    onExpandedChange = { chairExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedChair?.name ?: "Select Chair",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assigned Chair") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = chairExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = chairExpanded,
                        onDismissRequest = { chairExpanded = false }
                    ) {
                        chairs.forEach { chair ->
                            DropdownMenuItem(
                                text = { Text(chair.name) },
                                onClick = {
                                    selectedChair = chair
                                    chairExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date & Time
                Text("Schedule Timing", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = startTimeStr,
                        onValueChange = { startTimeStr = it },
                        label = { Text("Start Time (HH:mm)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Duration Selector Chips
                Text("Duration: $durationMinutes min (Ends at $endTimeStr)", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(15, 30, 45, 60, 90).forEach { mins ->
                        FilterChip(
                            selected = durationMinutes == mins,
                            onClick = { durationMinutes = mins },
                            label = { Text("${mins}m") }
                        )
                    }
                }

                // Conflict Banner Warning if double booked
                if (conflictWarning != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Conflict",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = conflictWarning ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clinical Type & Procedure
                Text("Procedure & Treatment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Appointment Title / Reason") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = procedureName,
                        onValueChange = { procedureName = it },
                        label = { Text("Procedure Name") },
                        modifier = Modifier.weight(2f)
                    )

                    OutlinedTextField(
                        value = toothNumberText,
                        onValueChange = { toothNumberText = it },
                        label = { Text("Tooth #") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical Notes / Instructions") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
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
                            val doc = selectedDoctor ?: return@Button
                            val toothNum = toothNumberText.toIntOrNull()
                            onBook(
                                patientId,
                                patientName,
                                patientPhone,
                                doc.id,
                                doc.name,
                                selectedChair?.id,
                                selectedChair?.name,
                                title,
                                procedureName.ifBlank { null },
                                toothNum,
                                dateStr,
                                startTimeStr,
                                endTimeStr,
                                selectedType,
                                notes.ifBlank { null }
                            )
                        },
                        enabled = patientName.isNotBlank() && selectedDoctor != null && conflictWarning == null
                    ) {
                        Text("Book Appointment")
                    }
                }
            }
        }
    }
}

private fun calculateEndTime(startTime: String, durationMinutes: Int): String {
    return try {
        val parts = startTime.split(":")
        val startMins = parts[0].toInt() * 60 + parts[1].toInt()
        val endMins = startMins + durationMinutes
        val endHour = (endMins / 60) % 24
        val endMin = endMins % 60
        "${endHour.toString().padStart(2, '0')}:${endMin.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        "10:00"
    }
}
