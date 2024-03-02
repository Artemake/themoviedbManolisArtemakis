package com.manosprojects.themoviedb.features.home.domain.data

import android.graphics.Bitmap

data class DMovie(
    val movieId: Int,
    val title: String,
    val releaseDate: String,
    val rating: Float,
    val image: Bitmap?,
)
