package org.lelestacia.posle.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Instant

@Suppress("NewApi")
fun Long.toFormattedDateTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val javaLocalDateTime = localDateTime.toJavaLocalDateTime()
    val locale = Locale.forLanguageTag("id-ID")

    val day = javaLocalDateTime.dayOfMonth
    val month = javaLocalDateTime.month.getDisplayName(TextStyle.FULL, locale)
        .replaceFirstChar { it.uppercase() }
    val year = javaLocalDateTime.year
    val hour = javaLocalDateTime.hour.toString().padStart(2, '0')
    val minute = javaLocalDateTime.minute.toString().padStart(2, '0')

    return "$day $month $year - $hour:$minute"
}

@Suppress("NewApi")
fun Long.toFormattedDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val javaLocalDateTime = localDateTime.toJavaLocalDateTime()
    val locale = Locale.forLanguageTag("id-ID")

    val day = javaLocalDateTime.dayOfMonth
    val month = javaLocalDateTime.month.getDisplayName(TextStyle.FULL, locale)
        .replaceFirstChar { it.uppercase() }
    val year = javaLocalDateTime.year

    return "$day $month $year"
}