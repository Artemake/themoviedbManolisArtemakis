package com.manosprojects.themoviedb.domain.source.remote.data

data class MovieDetailsResponse(
    val id: Long,
    val title: String,
    val genres: List<Genre>,
    val release_date: String,
    val vote_average: Float,
    val backdrop_path: String,
    val runtime: Int,
    val overview: String,
)

data class Genre(
    val name: String,
)