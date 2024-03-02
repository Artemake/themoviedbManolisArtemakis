package com.manosprojects.themoviedb.features.home.domain.source.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

    private val baseUrl = "https://image.tmdb.org/t/p/w300"

    override suspend fun loadMovies(): List<DMovie>? {
        return try {
            moviesAPI.getMovies(pageCount).results.map {
                val bitmap = downloadImage(it.backdrop_path)
                it.mapToDomain(bitmap)
            }.also {
                pageCount++
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun downloadImage(imagePath: String): Bitmap? {
        return try {
            val url = baseUrl + imagePath
            val responseBody = moviesAPI.downloadImage(url)
            val stream = responseBody.byteStream()
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            null
        }
    }

    private fun RMovie.mapToDomain(bitmap: Bitmap?): DMovie {
        return DMovie(
            movieId = id,
            title = title,
            releaseDate = release_date,
            rating = vote_average,
            image = bitmap,
        )
    }
}