package com.example.dentooth.feature.calendar.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.dentooth.core.designsystem.components.DentoothAvatar
import com.example.dentooth.core.designsystem.components.DentoothConfirmDialog
import com.example.dentooth.feature.calendar.domain.model.Appointment
import com.example.dentooth.feature.calendar.domain.model.AppointmentStatus

@Composable
fun AppointmentDetailDialog(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onStatusChange: (AppointmentStatus) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        DentoothConfirmDialog(
            title = "Delete Appointment",
            message = "Are you sure you want to delete this scheduled appointment for ${appointment.patientName}?",
            confirmText = "Delete",
            isDestructive = true,
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appointment.displayColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = appointment.status.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = appointment.displayColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Row {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title & Procedure
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (!appointment.procedureName.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = appointment.procedureName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (appointment.toothNumber != null) {
                    Text(
                        text = "Tooth #${appointment.toothNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Patient Information Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DentoothAvatar(name = appointment.patientName, size = 40.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = appointment.patientName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (appointment.patientPhone.isNotBlank()) {
                                Text(
                                    text = appointment.patientPhone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Schedule Timing Details
                DetailRow(
                    label = "Date & Time",
                    value = "${appointment.date} • ${appointment.startTime} - ${appointment.endTime} (${appointment.durationMinutes} min)"
                )
                DetailRow(
                    label = "Attending Doctor",
                    value = appointment.doctorName
                )
                DetailRow(
                    label = "Chair / Operatory",
                    value = appointment.chairName ?: "General Chair"
                )
                DetailRow(
                    label = "Appointment Type",
                    value = appointment.type.label
                )

                if (!appointment.notes.isNullOrBlank()) {
                    DetailRow(
                        label = "Clinical Notes",
                        value = appointment.notes
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Status Update Controls
                Text(
                    text = "Update Appointment Status",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (appointment.status != AppointmentStatus.CONFIRMED && appointment.status != AppointmentStatus.COMPLETED) {
                        FilledTonalButton(
                            onClick = { onStatusChange(AppointmentStatus.CONFIRMED) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Check-In", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (appointment.status != AppointmentStatus.IN_PROGRESS && appointment.status != AppointmentStatus.COMPLETED) {
                        FilledTonalButton(
                            onClick = { onStatusChange(AppointmentStatus.IN_PROGRESS) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("In Chair", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (appointment.status != AppointmentStatus.COMPLETED) {
                        Button(
                            onClick = { onStatusChange(AppointmentStatus.COMPLETED) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Complete", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (appointment.status != AppointmentStatus.CANCELLED && appointment.status != AppointmentStatus.COMPLETED) {
                        OutlinedButton(
                            onClick = { onStatusChange(AppointmentStatus.CANCELLED) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (appointment.status != AppointmentStatus.NO_SHOW && appointment.status != AppointmentStatus.COMPLETED) {
                        OutlinedButton(
                            onClick = { onStatusChange(AppointmentStatus.NO_SHOW) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("No-Show", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
