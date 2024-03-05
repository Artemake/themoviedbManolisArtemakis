package com.manosprojects.themoviedb.domain.source.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.data.DReview
import com.manosprojects.themoviedb.domain.source.remote.api.MoviesAPI
import com.manosprojects.themoviedb.domain.source.remote.data.MovieDetailsResponse
import com.manosprojects.themoviedb.domain.source.remote.data.ReviewsResponse
import com.manosprojects.themoviedb.domain.source.remote.mappers.mapToDomain
import com.manosprojects.themoviedb.utils.formatRDurationToDuration
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MoviesRemoteSource {
    fun loadMovies(): Flow<List<DMovie>?>
    fun loadMovieDetails(movieId: Long): Flow<DMovieDetails?>
    fun incrementPage()
}

class MoviesRemoteSourceImpl @Inject constructor(
    private val moviesAPI: MoviesAPI
) : MoviesRemoteSource {

    private var pageCount = 1

    // ideally we would fetch the info for this regarding the size of the image from the dedicated
    // endpoint
    private val baseUrl = "https://image.tmdb.org/t/p/original"

    override fun loadMovies(): Flow<List<DMovie>?> {
        return flow {
            try {
                val list: MutableList<DMovie> = mutableListOf()
                moviesAPI.getMovies(pageCount).results.map {
                    list.add(it.mapToDomain(baseUrl + it.backdrop_path))
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
                val similarMoviesResponse = moviesAPI.getSimilarMovies(movieId = movieId)
                val reviewsResponse = moviesAPI.getReviews(movieId = movieId)
                val dMovies = mutableListOf<DMovie>()
                similarMoviesResponse.results.filter { it.poster_path != null }.forEach { rMovie ->
                    dMovies.add(rMovie.mapToDomain(baseUrl + rMovie.poster_path))
                    emit(
                        getMovieDetails(
                            movieDetailsResponse = movieDetailsResponse,
                            reviewsResponse = reviewsResponse,
                            dMovies = dMovies,
                        )
                    )
                }
            } catch (e: Exception) {
                emit(null)
            }
        }
    }

    override fun incrementPage() {
        pageCount++
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

    private fun getMovieDetails(
        movieDetailsResponse: MovieDetailsResponse,
        reviewsResponse: ReviewsResponse,
        dMovies: List<DMovie>,
    ): DMovieDetails {
        return DMovieDetails(
            movieId = movieDetailsResponse.id,
            title = movieDetailsResponse.title,
            releaseDate = formatStringDateToLocalDate(movieDetailsResponse.release_date),
            rating = movieDetailsResponse.vote_average,
            imageUrl = baseUrl + movieDetailsResponse.backdrop_path,
            genres = movieDetailsResponse.genres.map { it.name },
            runtime = formatRDurationToDuration(movieDetailsResponse.runtime),
            description = movieDetailsResponse.overview,
            reviews = reviewsResponse.results.map {
                DReview(
                    author = it.author,
                    content = it.content
                )
            },
            similarMovies = dMovies,
        )
    }

}