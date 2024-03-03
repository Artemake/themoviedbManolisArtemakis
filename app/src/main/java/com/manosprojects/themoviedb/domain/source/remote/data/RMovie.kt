package com.manosprojects.themoviedb.domain.source.remote.data

data class RMovie(
    val id: Int,
    val title: String,
    val release_date: String,
    val vote_average: Float,
    val backdrop_path: String,
)