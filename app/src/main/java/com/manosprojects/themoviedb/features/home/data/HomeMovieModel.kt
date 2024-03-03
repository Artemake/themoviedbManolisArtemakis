package com.manosprojects.themoviedb.features.home.data

import android.graphics.Bitmap

data class HomeMovieModel(
    val movieId: Long,
    val title: String,
    val releaseDate: String,
    val rating: Float,
    val isFavorite: Boolean,
    val image: Bitmap?,
)
