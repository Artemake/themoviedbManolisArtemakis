package com.manosprojects.themoviedb.domain.source.local.mappers

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.data.LMovie
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate

fun LMovie.mapToDomain(): DMovie {
    return DMovie(
        movieId = movieId,
        title = title,
        releaseDate = formatStringDateToLocalDate(releaseDate),
        rating = rating,
        imageUrl = imageFile,
        isFavourite = isFavourite,
    )
}