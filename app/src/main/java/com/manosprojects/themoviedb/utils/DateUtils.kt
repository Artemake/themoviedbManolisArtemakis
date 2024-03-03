package com.manosprojects.themoviedb.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun formatDMovieDateToUIDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    return formatter.format(date)
}

fun formatRMovieDateToLocalDate(date: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(date, formatter)
}