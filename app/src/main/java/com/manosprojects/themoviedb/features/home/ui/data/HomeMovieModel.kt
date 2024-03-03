package com.manosprojects.themoviedb.features.home.ui.data

import android.graphics.Bitmap

data class HomeMovieModel(
    val movieId: Int,
    val title: String,
    val releaseDate: String,
    val rating: Float,
    val isFavorite: Boolean,
    val image: Bitmap?,
)
