package com.manosprojects.themoviedb.utils

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun formatDurationToUITime(duration: Duration): String {
    val hours = duration.inWholeHours
    val minutes = duration.minus(hours.toDuration(DurationUnit.HOURS)).inWholeMinutes
    return "$hours h $minutes min"
}

fun formatRDurationToDuration(minutes: Int): Duration {
    return minutes.toDuration(DurationUnit.MINUTES)
}