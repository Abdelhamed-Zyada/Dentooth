package com.example.dentooth.feature.calendar.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentooth.core.designsystem.DentoothColors
import com.example.dentooth.core.util.DateTimeUtil
import com.example.dentooth.feature.calendar.domain.model.Appointment
import kotlinx.datetime.LocalDate

@Composable
fun WeekCalendarView(
    referenceDate: LocalDate,
    appointments: List<Appointment>,
    onSelectDate: (LocalDate) -> Unit,
    onAppointmentClick: (Appointment) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val today = DateTimeUtil.today()

    // Generate 7 days around reference date (-3 to +3)
    val weekDays = (-3..3).map { offset ->
        LocalDate.fromEpochDays(referenceDate.toEpochDays() + offset)
    }

    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()
    val columnWidth = 140.dp

    Column(modifier = modifier.fillMaxSize()) {
        // Week Days Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(vertical = 8.dp)
                .horizontalScroll(horizontalScroll)
        ) {
            weekDays.forEach { date ->
                val isSelected = date == referenceDate
                val isToday = date == today
                val dayOfWeek = dayNames[date.dayOfWeek.ordinal % 7]

                Column(
                    modifier = Modifier
                        .width(columnWidth)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelectDate(date) }
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayOfWeek,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                    else -> Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        // Week Days Columns with Cards
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScroll)
                .horizontalScroll(horizontalScroll)
                .padding(vertical = 8.dp)
        ) {
            weekDays.forEach { date ->
                val dateStr = date.toString()
                val dayApts = appointments.filter { it.date == dateStr }.sortedBy { it.startTime }

                Column(
                    modifier = Modifier
                        .width(columnWidth)
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp)
                ) {
                    if (dayApts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No visits",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        dayApts.forEach { apt ->
                            WeekAppointmentCard(
                                appointment = apt,
                                onClick = { onAppointmentClick(apt) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekAppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = appointment.displayColor

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = statusColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "${appointment.startTime} - ${appointment.endTime}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = appointment.patientName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
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

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.doctorName.replace("Dr. ", ""),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }
        }
    }
}
