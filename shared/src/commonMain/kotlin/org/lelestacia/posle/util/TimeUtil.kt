package org.lelestacia.posle.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Instant

/**
 * Calculates the start and end of the current calendar day in milliseconds.
 *
 * @return A [Pair] containing (Start of Today, Start of Tomorrow) in epoch milliseconds.
 */
fun getTodayRangeMilliseconds(): Pair<Long, Long> {
    val todayZone = ZoneId.systemDefault()
    val startOfDayZdt = ZonedDateTime.now(todayZone).toLocalDate().atStartOfDay(todayZone)
    val startOfDayMs = startOfDayZdt.toInstant().toEpochMilli()
    val tomorrowZdt = startOfDayZdt.plusDays(1)
    val startOfTomorrowMs = tomorrowZdt.toInstant().toEpochMilli()
    return Pair(startOfDayMs, startOfTomorrowMs)
}

/**
 * Converts an epoch millisecond timestamp to a [LocalDate].
 *
 * @param timeZone The time zone to use for conversion.
 */
fun Long.toLocalDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDate {
    return Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(timeZone)
        .date
}

/**
 * Normalizes a timestamp to the exact start of its day (00:00:00.000).
 *
 * @param timeZone The time zone context.
 */
fun Long.startOfDayEpochMilliseconds(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Long {
    return Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(timeZone)
        .date
        .atStartOfDayIn(timeZone)
        .toEpochMilliseconds()
}

/**
 * Normalizes a timestamp to the exact end of its day (23:59:59.999).
 *
 * @param timeZone The time zone context.
 */
fun Long.endOfDayEpochMilliseconds(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): Long {
    val startOfNextDay = Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(timeZone)
        .date
        .plus(DatePeriod(days = 1))
        .atStartOfDayIn(timeZone)

    return startOfNextDay.toEpochMilliseconds() - 1
}