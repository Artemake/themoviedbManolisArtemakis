package com.manosprojects.themoviedb.domain.source.local.mappers

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.data.LMovie
import com.manosprojects.themoviedb.utils.formatLocalDateToLDate

fun DMovie.mapToCache(): LMovie {
    return LMovie(
        movieId = movieId,
        title = title,
        releaseDate = formatLocalDateToLDate(releaseDate),
        rating = rating,
        isFavourite = isFavourite,
    )
}