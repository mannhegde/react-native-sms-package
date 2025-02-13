package com.smspackage

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


fun String.toZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}

fun ZonedDateTime.toISO8601String(): String {
    return format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}

fun Long.toZonedDateTime(milliseconds: Boolean = false, zone: ZoneId? = null): ZonedDateTime {
    val instant = if (milliseconds) Instant.ofEpochMilli(this) else Instant.ofEpochSecond(this)
    return instant.atZone(zone ?: ZoneId.of("UTC"))
}

// Extension function to convert ZonedDateTime to epoch milliseconds
fun ZonedDateTime.toEpochMilli(): Long {
    return this.toInstant().toEpochMilli()
}
