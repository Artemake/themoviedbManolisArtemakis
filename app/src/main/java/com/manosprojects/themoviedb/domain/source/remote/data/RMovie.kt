package com.manosprojects.themoviedb.domain.source.remote.data

data class RMovie(
    val id: Long,
    val title: String,
    val release_date: String,
    val vote_average: Float,
    val backdrop_path: String,
    val poster_path: String,
)
