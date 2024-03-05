package com.manosprojects.themoviedb.features.home.data

data class HomeMovieModel(
    val movieId: Long,
    val title: String,
    val releaseDate: String,
    val rating: Float,
    val isFavorite: Boolean,
    val imageUrl: String,
)
