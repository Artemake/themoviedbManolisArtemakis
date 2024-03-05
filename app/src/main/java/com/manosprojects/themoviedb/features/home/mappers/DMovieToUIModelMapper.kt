package com.manosprojects.themoviedb.features.home.mappers

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.features.home.data.HomeMovieModel
import com.manosprojects.themoviedb.utils.formatDomainDateToUIDate

fun DMovie.mapToUIModel(): HomeMovieModel {
    return HomeMovieModel(
        title = title,
        movieId = movieId,
        releaseDate = formatDomainDateToUIDate(releaseDate),
        rating = rating,
        imageUrl = imageUrl,
        isFavorite = isFavourite,
    )
}