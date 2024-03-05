package com.manosprojects.themoviedb.domain.source.local.mappers

import android.graphics.Bitmap
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.data.LMovie
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate

fun LMovie.mapToDomain(image: Bitmap?): DMovie {
    return DMovie(
        movieId = movieId,
        title = title,
        releaseDate = formatStringDateToLocalDate(releaseDate),
        rating = rating,
        image = image,
        isFavourite = isFavourite,
    )
}