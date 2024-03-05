package com.manosprojects.themoviedb.domain.data

import java.time.LocalDate
import kotlin.time.Duration

data class DMovieDetails(
    val movieId: Long,
    val title: String,
    val releaseDate: LocalDate,
    val rating: Float,
    val imageUrl: String,
    val isFavourite: Boolean = false,
    val genres: List<String>,
    val runtime: Duration,
    val description: String,
    val reviews: List<DReview>,
    val similarMovies: List<DMovie>
)

data class DReview(
    val author: String,
    val content: String,
)