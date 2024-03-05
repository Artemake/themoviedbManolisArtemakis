package com.manosprojects.themoviedb.domain.source.remote

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.data.DReview
import com.manosprojects.themoviedb.domain.source.remote.api.MoviesAPI
import com.manosprojects.themoviedb.domain.source.remote.data.MovieDetailsResponse
import com.manosprojects.themoviedb.domain.source.remote.data.ReviewsResponse
import com.manosprojects.themoviedb.domain.source.remote.mappers.mapToDomain
import com.manosprojects.themoviedb.utils.formatRDurationToDuration
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate
import javax.inject.Inject

interface MoviesRemoteSource {
    suspend fun loadMovies(): List<DMovie>?
    suspend fun loadMovieDetails(movieId: Long): DMovieDetails?
    fun incrementPage()
}

class MoviesRemoteSourceImpl @Inject constructor(
    private val moviesAPI: MoviesAPI
) : MoviesRemoteSource {

    private var pageCount = 1

    // ideally we would fetch the info for this regarding the size of the image from the dedicated
    // endpoint

    private val baseUrl = "https://image.tmdb.org/t/p/original"

    override suspend fun loadMovies(): List<DMovie>? {
        return try {
            moviesAPI.getMovies(pageCount).results.map { rMovie ->
                rMovie.mapToDomain(baseUrl + rMovie.backdrop_path)
            }.also { pageCount++ }
        } catch (e: Exception) {
            null
        }

    }

    override suspend fun loadMovieDetails(movieId: Long): DMovieDetails? {
        return try {
            val movieDetailsResponse = moviesAPI.getMovieDetails(movieId = movieId)
            val similarMoviesResponse = moviesAPI.getSimilarMovies(movieId = movieId)
            val reviewsResponse = moviesAPI.getReviews(movieId = movieId)
            val dMovies =
                similarMoviesResponse.results.filter { it.poster_path != null }.map { rMovie ->
                    rMovie.mapToDomain(baseUrl + rMovie.poster_path)

                }
            getMovieDetails(
                movieDetailsResponse = movieDetailsResponse,
                reviewsResponse = reviewsResponse,
                dMovies = dMovies,
            )
        } catch (e: Exception) {
            null
        }
    }


    override fun incrementPage() {
        pageCount++
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