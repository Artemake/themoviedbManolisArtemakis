package com.manosprojects.themoviedb.domain.data

import java.time.LocalDate

data class DMovie(
    val movieId: Long,
    val title: String,
    val releaseDate: LocalDate,
    val rating: Float,
    val imageUrl: String,
    val isFavourite: Boolean = false,
)
