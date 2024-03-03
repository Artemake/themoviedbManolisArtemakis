package com.manosprojects.themoviedb.domain.source.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.data.DReview
import com.manosprojects.themoviedb.domain.source.remote.api.MoviesAPI
import com.manosprojects.themoviedb.domain.source.remote.data.RMovie
import com.manosprojects.themoviedb.utils.formatRMovieDateToLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MoviesRemoteSource {
    fun loadMovies(): Flow<List<DMovie>?>
    fun loadMovieDetails(movieId: Long): Flow<DMovieDetails?>
}

class MoviesRemoteSourceImpl @Inject constructor(
    private val moviesAPI: MoviesAPI
) : MoviesRemoteSource {

    private var pageCount = 1

    // TODO: to remove and adjust this to place it in the NetworkDI
    private val baseUrl = "https://image.tmdb.org/t/p/original"

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

    override fun loadMovieDetails(movieId: Long): Flow<DMovieDetails?> {
        return flow {
            try {
                val movieDetailsResponse = moviesAPI.getMovieDetails(movieId = movieId)
                val image = downloadImage(movieDetailsResponse.backdrop_path)
                val similarMoviesResponse = moviesAPI.getSimilarMovies(movieId = movieId)
                val reviewsResponse = moviesAPI.getReviews(movieId = movieId)
                val dMovies = mutableListOf<DMovie>()
                similarMoviesResponse.results.map { rMovie ->
                    val bitmap = downloadImage(rMovie.backdrop_path)
                    bitmap?.let { bitmapNotNull ->
                        dMovies.add(rMovie.mapToDomain(bitmapNotNull))
                        emit(
                            DMovieDetails(
                                movieId = movieDetailsResponse.id,
                                title = movieDetailsResponse.title,
                                releaseDate = formatRMovieDateToLocalDate(movieDetailsResponse.release_date),
                                rating = movieDetailsResponse.vote_average,
                                image = image,
                                genres = movieDetailsResponse.genres.map { it.name },
                                runtime = movieDetailsResponse.runtime,
                                description = movieDetailsResponse.overview,
                                reviews = reviewsResponse.results.map {
                                    DReview(
                                        author = it.author,
                                        content = it.content
                                    )
                                },
                                similarMovies = dMovies,
                            )
                        )
                    }
                }
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
            releaseDate = formatRMovieDateToLocalDate(release_date),
            rating = vote_average,
            image = bitmap,
        )
    }
}