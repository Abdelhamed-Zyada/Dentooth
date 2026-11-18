package com.example.dentooth.core.util

import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateTimeUtil {
    fun currentInstant(): Instant =
        Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())

    fun today(): LocalDate =
        currentInstant().toLocalDateTime(TimeZone.currentSystemDefault()).date

    fun currentLocalDateTime(): LocalDateTime =
        currentInstant().toLocalDateTime(TimeZone.currentSystemDefault())

    fun formatDate(date: LocalDate): String {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val month = monthNames.getOrElse(date.monthNumber - 1) { "" }
        return "$month ${date.dayOfMonth}, ${date.year}"
    }

    fun formatDateTime(instant: Instant): String {
        val ldt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${formatDate(ldt.date)} ${formatTime(ldt)}"
    }

    fun formatTime(ldt: LocalDateTime): String {
        val hour = ldt.hour
        val minute = ldt.minute.toString().padStart(2, '0')
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "$displayHour:$minute $amPm"
    }

    fun calculateAge(birthDate: LocalDate): Int {
        val currentDate = today()
        var age = currentDate.year - birthDate.year
        if (currentDate.monthNumber < birthDate.monthNumber ||
            (currentDate.monthNumber == birthDate.monthNumber && currentDate.dayOfMonth < birthDate.dayOfMonth)
        ) {
            age--
        }
        return age.coerceAtLeast(0)
    }

    fun isToday(date: LocalDate): Boolean = date == today()
}
