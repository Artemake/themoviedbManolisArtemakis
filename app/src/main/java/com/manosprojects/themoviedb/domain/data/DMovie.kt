package com.manosprojects.themoviedb.domain.data

import android.graphics.Bitmap
import java.time.LocalDate

data class DMovie(
    val movieId: Int,
    val title: String,
    val releaseDate: LocalDate,
    val rating: Float,
    val image: Bitmap?,
    val isFavourite: Boolean = false,
)
