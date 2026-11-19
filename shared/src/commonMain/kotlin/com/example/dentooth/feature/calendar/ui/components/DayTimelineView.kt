package com.example.dentooth.feature.calendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.feature.calendar.domain.model.Appointment
import com.example.dentooth.feature.calendar.domain.model.Chair
import com.example.dentooth.feature.calendar.domain.model.StaffDoctor
import com.example.dentooth.feature.calendar.ui.AppointmentBookingPrefill
import com.example.dentooth.feature.calendar.ui.CalendarColumnMode

@Composable
fun DayTimelineView(
    dateStr: String,
    columnMode: CalendarColumnMode,
    chairs: List<Chair>,
    doctors: List<StaffDoctor>,
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit,
    onEmptySlotClick: (AppointmentBookingPrefill) -> Unit,
    modifier: Modifier = Modifier
) {
    val startHour = 8   // 8:00 AM
    val endHour = 20    // 8:00 PM
    val totalHours = endHour - startHour
    val hourHeight = 72.dp
    val timeColumnWidth = 60.dp
    val operatoryColumnWidth = 190.dp

    val columns = if (columnMode == CalendarColumnMode.BY_CHAIR) {
        chairs.map { ColumnHeader(id = it.id, title = it.name, subtitle = "Chair", color = DentoothColors.Primary) }
    } else {
        doctors.map { ColumnHeader(id = it.id, title = it.name, subtitle = it.specialization, color = it.composeColor) }
    }

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        // Sticky Header with columns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 8.dp)
                .horizontalScroll(horizontalScroll)
        ) {
            Spacer(modifier = Modifier.width(timeColumnWidth))
            columns.forEach { col ->
                Column(
                    modifier = Modifier
                        .width(operatoryColumnWidth)
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = col.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = col.color
                    )
                    Text(
                        text = col.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        HorizontalDivider()

        // Timeline Grid Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
                .horizontalScroll(horizontalScroll)
        ) {
            // Background hour lines & time labels
            Column {
                for (h in startHour until endHour) {
                    val displayTime = formatHour(h)
                    Row(
                        modifier = Modifier
                            .height(hourHeight)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = displayTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .width(timeColumnWidth)
                                .padding(top = 4.dp, start = 8.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Interactive columns & appointment cards
            Row(modifier = Modifier.padding(start = timeColumnWidth)) {
                columns.forEach { col ->
                    Box(
                        modifier = Modifier
                            .width(operatoryColumnWidth)
                            .height(hourHeight * totalHours)
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                    ) {
                        // Clickable hour slot zones for booking
                        for (h in startHour until endHour) {
                            val slotTime = "${h.toString().padStart(2, '0')}:00"
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(hourHeight)
                                    .padding(top = hourHeight * (h - startHour))
                                    .clickable {
                                        onEmptySlotClick(
                                            AppointmentBookingPrefill(
                                                date = dateStr,
                                                startTime = slotTime,
                                                chairId = if (columnMode == CalendarColumnMode.BY_CHAIR) col.id else null,
                                                doctorId = if (columnMode == CalendarColumnMode.BY_DOCTOR) col.id else null
                                            )
                                        )
                                    }
                            )
                        }

                        // Filter appointments matching this column
                        val matchingApts = appointments.filter { apt ->
                            if (columnMode == CalendarColumnMode.BY_CHAIR) {
                                apt.chairId == col.id
                            } else {
                                apt.doctorId == col.id
                            }
                        }

                        matchingApts.forEach { apt ->
                            val (topOffsetDp, blockHeightDp) = calculateAppointmentPosition(
                                startTime = apt.startTime,
                                endTime = apt.endTime,
                                startHour = startHour,
                                hourHeight = hourHeight
                            )

                            AppointmentTimelineCard(
                                appointment = apt,
                                onClick = { onAppointmentClick(apt) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 3.dp, vertical = 2.dp)
                                    .height(blockHeightDp)
                                    .padding(top = topOffsetDp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentTimelineCard(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = appointment.displayColor

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = statusColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.8f)),
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Status color indicator stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${appointment.startTime} - ${appointment.endTime}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(statusColor.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = appointment.status.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = statusColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = appointment.patientName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!appointment.procedureName.isNullOrBlank()) {
                        Text(
                            text = appointment.procedureName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (appointment.toothNumber != null) {
                    Text(
                        text = "Tooth #${appointment.toothNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private data class ColumnHeader(
    val id: String,
    val title: String,
    val subtitle: String,
    val color: Color
)

private fun formatHour(hour: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$displayHour $amPm"
}

private fun calculateAppointmentPosition(
    startTime: String,
    endTime: String,
    startHour: Int,
    hourHeight: androidx.compose.ui.unit.Dp
): Pair<androidx.compose.ui.unit.Dp, androidx.compose.ui.unit.Dp> {
    fun timeToMinutes(t: String): Int {
        return try {
            val parts = t.split(":")
            parts[0].toInt() * 60 + parts[1].toInt()
        } catch (e: Exception) {
            0
        }
    }

    val baseMinutes = startHour * 60
    val startMin = timeToMinutes(startTime).coerceAtLeast(baseMinutes)
    val endMin = timeToMinutes(endTime).coerceAtLeast(startMin + 15)

    val topMinutes = (startMin - baseMinutes).coerceAtLeast(0)
    val durationMinutes = (endMin - startMin).coerceAtLeast(15)

    // 60 minutes = hourHeight
    val topOffset = (hourHeight.value * (topMinutes / 60f)).dp
    val blockHeight = (hourHeight.value * (durationMinutes / 60f)).coerceAtLeast(42f).dp

    return Pair(topOffset, blockHeight)
}
