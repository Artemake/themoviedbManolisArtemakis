package com.manosprojects.themoviedb.domain.source.remote.mappers

import android.graphics.Bitmap
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.remote.data.RMovie
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate

fun RMovie.mapToDomain(bitmap: Bitmap?): DMovie {
    return DMovie(
        movieId = id,
        title = title,
        releaseDate = formatStringDateToLocalDate(release_date),
        rating = vote_average,
        image = bitmap,
    )
}