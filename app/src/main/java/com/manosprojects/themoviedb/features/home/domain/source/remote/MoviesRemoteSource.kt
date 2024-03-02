package com.manosprojects.themoviedb.features.home.domain.source.remote

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import com.manosprojects.themoviedb.features.home.domain.source.remote.api.MoviesAPI
import com.manosprojects.themoviedb.features.home.domain.source.remote.data.RMovie
import javax.inject.Inject

interface MoviesRemoteSource {
    suspend fun loadMovies(): List<DMovie>?
}

class MoviesRemoteSourceImpl @Inject constructor(
    private val moviesAPI: MoviesAPI
) : MoviesRemoteSource {

    private var pageCount = 1

    override suspend fun loadMovies(): List<DMovie>? {
        return try {
            moviesAPI.getMovies(pageCount).results.map {
                it.mapToDomain()
            }.also {
                pageCount++
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun RMovie.mapToDomain(): DMovie {
        return DMovie(
            movieId = id,
            title = title,
            releaseDate = release_date,
            rating = vote_average,
        )
    }
}