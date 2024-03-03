package com.manosprojects.themoviedb.domain.data

import android.graphics.Bitmap
import java.time.LocalDate

data class DMovieDetails(
    val movieId: Long,
    val title: String,
    val releaseDate: LocalDate,
    val rating: Float,
    val image: Bitmap?,
    val isFavourite: Boolean = false,
    val genres: List<String>,
    val runtime: Int,
    val description: String,
    val reviews: List<DReview>,
    val similarMovies: List<DMovie>
)

data class DReview(
    val author: String,
    val content: String,
)