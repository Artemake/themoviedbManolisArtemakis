package com.manosprojects.themoviedb.features.home.domain.source.remote.data

data class RMovie(
    val id: Int,
    val title: String,
    val release_date: String,
    val vote_average: Float,
)
