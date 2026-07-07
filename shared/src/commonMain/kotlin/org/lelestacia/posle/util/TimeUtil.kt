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

fun getTodayRangeMilliseconds(): Pair<Long, Long> {
    val todayZone = ZoneId.systemDefault()
    val startOfDayZdt = ZonedDateTime.now(todayZone).toLocalDate().atStartOfDay(todayZone)
    val startOfDayMs = startOfDayZdt.toInstant().toEpochMilli()
    val tomorrowZdt = startOfDayZdt.plusDays(1)
    val startOfTomorrowMs = tomorrowZdt.toInstant().toEpochMilli()
    return Pair(startOfDayMs, startOfTomorrowMs)
}

fun Long.toLocalDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDate {
    return Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(timeZone)
        .date
}

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