package com.example.dentooth.feature.calendar.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentooth.core.designsystem.components.DentoothAvatar
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.calendar.domain.model.Appointment
import com.example.dentooth.feature.calendar.domain.model.AppointmentStatus

@Composable
fun AgendaView(
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit,
    onStatusChange: (String, AppointmentStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    if (appointments.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No appointments found in agenda",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    // Group by date
    val grouped = appointments.groupBy { it.date }
    val todayStr = DateTimeUtil.today().toString()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        grouped.forEach { (dateStr, dateApts) ->
            item(key = "header-$dateStr") {
                val headerTitle = when (dateStr) {
                    todayStr -> "Today • $dateStr"
                    else -> dateStr
                }

                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (dateStr == todayStr) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }

            items(dateApts, key = { it.id }) { apt ->
                AgendaAppointmentCard(
                    appointment = apt,
                    onClick = { onAppointmentClick(apt) },
                    onStatusChange = { newStatus -> onStatusChange(apt.id, newStatus) }
                )
            }
        }
    }
}

@Composable
private fun AgendaAppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
    onStatusChange: (AppointmentStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = appointment.displayColor

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DentoothAvatar(
                    name = appointment.patientName,
                    size = 42.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = appointment.patientName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = appointment.status.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (appointment.patientPhone.isNotBlank()) {
                        Text(
                            text = appointment.patientPhone,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            // Visit Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = appointment.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${appointment.doctorName} • ${appointment.chairName ?: "General Chair"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${appointment.startTime} - ${appointment.endTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${appointment.durationMinutes} min • ${appointment.type.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Quick Status Actions
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (appointment.status) {
                    AppointmentStatus.SCHEDULED -> {
                        FilledTonalButton(
                            onClick = { onStatusChange(AppointmentStatus.CONFIRMED) },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Confirm / Check-in", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    AppointmentStatus.CONFIRMED -> {
                        FilledTonalButton(
                            onClick = { onStatusChange(AppointmentStatus.IN_PROGRESS) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Start Treatment", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    AppointmentStatus.IN_PROGRESS -> {
                        FilledTonalButton(
                            onClick = { onStatusChange(AppointmentStatus.COMPLETED) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFE8F5E9),
                                contentColor = Color(0xFF2E7D32)
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Mark Completed", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
