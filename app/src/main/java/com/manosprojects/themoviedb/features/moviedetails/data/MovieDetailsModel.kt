package com.manosprojects.themoviedb.features.moviedetails.data

import android.graphics.Bitmap

data class MovieDetailsModel(
    val movieId: Long,
    val title: String,
    val releaseDate: String,
    val rating: Float,
    val image: Bitmap?,
    val isFavourite: Boolean = false,
    val genres: List<String>,
    val runtime: Int,
    val description: String,
    val reviews: List<MovieDetailsReviewModel>,
    val similarMovies: List<Bitmap>
)

data class MovieDetailsReviewModel(
    val author: String,
    val content: String,
)
