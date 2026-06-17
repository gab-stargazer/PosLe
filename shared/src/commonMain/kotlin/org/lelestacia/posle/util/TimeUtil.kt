package org.lelestacia.posle.util

import java.time.ZoneId
import java.time.ZonedDateTime

fun getTodayRangeMilliseconds(): Pair<Long, Long> {
    val todayZone = ZoneId.systemDefault()
    val startOfDayZdt = ZonedDateTime.now(todayZone).toLocalDate().atStartOfDay(todayZone)
    val startOfDayMs = startOfDayZdt.toInstant().toEpochMilli()
    val tomorrowZdt = startOfDayZdt.plusDays(1)
    val startOfTomorrowMs = tomorrowZdt.toInstant().toEpochMilli()
    return Pair(startOfDayMs, startOfTomorrowMs)
}