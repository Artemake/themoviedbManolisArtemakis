package com.manosprojects.themoviedb.domain.source.local.data

data class LMovie(
    val movieId: Long,
    val title: String,
    val releaseDate: String,
    val rating: Float,
    val isFavourite: Boolean,
    val imageFile: String,
)
