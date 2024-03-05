package com.manosprojects.themoviedb.domain.source.remote.mappers

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.remote.data.RMovie
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate

fun RMovie.mapToDomain(imageUrl: String): DMovie {
    return DMovie(
        movieId = id,
        title = title,
        releaseDate = formatStringDateToLocalDate(release_date),
        rating = vote_average,
        imageUrl = imageUrl,
    )
}