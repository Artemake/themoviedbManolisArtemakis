package com.manosprojects.themoviedb.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun formatDomainDateToUIDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    return formatter.format(date)
}

fun formatStringDateToLocalDate(date: String): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(date, formatter)
}

fun formatLocalDateToLDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return formatter.format(date)
}

