package com.manosprojects.themoviedb.features.home.domain.source.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import com.manosprojects.themoviedb.features.home.domain.source.remote.api.MoviesAPI
import com.manosprojects.themoviedb.features.home.domain.source.remote.data.RMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MoviesRemoteSource {
    fun loadMovies(): Flow<List<DMovie>?>
}

class MoviesRemoteSourceImpl @Inject constructor(
    private val moviesAPI: MoviesAPI
) : MoviesRemoteSource {

    private var pageCount = 1

    // TODO: to remove and adjust this to place it in the NetworkDI
    private val baseUrl = "https://image.tmdb.org/t/p/w300"

    override fun loadMovies(): Flow<List<DMovie>?> {
        return flow {
            try {
                val list: MutableList<DMovie> = mutableListOf()
                moviesAPI.getMovies(pageCount).results.map {
                    val bitmap = downloadImage(it.backdrop_path)
                    list.add(it.mapToDomain(bitmap))
                    emit(list)
                }
                pageCount++
            } catch (e: Exception) {
                emit(null)
            }
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