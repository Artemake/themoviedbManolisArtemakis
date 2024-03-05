package com.manosprojects.themoviedb.features.moviedetails.data

data class MovieDetailsModel(
    val movieId: Long,
    val title: String,
    val releaseDate: String,
    val rating: Float,
    val imageUrl: String,
    val isFavourite: Boolean = false,
    val genres: List<String>,
    val runtime: String,
    val description: String,
    val reviews: List<MovieDetailsReviewModel>,
    val similarMovies: List<String>
)

data class MovieDetailsReviewModel(
    val author: String,
    val content: String,
)
