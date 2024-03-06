package com.manosprojects.themoviedb.domain.source.remote

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.source.remote.api.MoviesAPI
import com.manosprojects.themoviedb.domain.source.remote.data.MovieDetailsResponse
import com.manosprojects.themoviedb.domain.source.remote.data.MoviesResponse
import com.manosprojects.themoviedb.domain.source.remote.data.RMovie
import com.manosprojects.themoviedb.domain.source.remote.data.ReviewsResponse
import com.manosprojects.themoviedb.domain.source.remote.mappers.mapToDomain
import com.manosprojects.themoviedb.utils.formatRDurationToDuration
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class MoviesRemoteSourceTest {

    private val moviesAPIMock: MoviesAPI = mockk()

    private val baseUrl = "https://image.tmdb.org/t/p/original"

    private lateinit var moviesRemoteSourceImpl: MoviesRemoteSourceImpl

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        moviesRemoteSourceImpl = MoviesRemoteSourceImpl(moviesAPIMock)
    }

    @Test
    fun `given MarvelAPI getMovies(pageCount) will return a list of movies, when loadMovies() is invoked, then assert results and verify interactions`() =
        runTest {
            // given
            coEvery { moviesAPIMock.getMovies(1) } returns MoviesResponse(
                listOf(
                    mockRMovie1(),
                    mockRMovie2()
                )
            )

            // when
            val actual = moviesRemoteSourceImpl.loadMovies()
            val expected = listOf(expectedDMovie1(), expectedDMovie2())

            // then
            assertThat(actual, equalTo(expected))
            coVerify { moviesAPIMock.getMovies(1) }
        }

    @Test
    fun `given MarvelAPI getMovies(pageCount) throws error, when loadMovies() is invoked, then assert results and verify interactions`() =
        runTest {
            coEvery { moviesAPIMock.getMovies(1) } throws Exception()

            // when
            val actual = moviesRemoteSourceImpl.loadMovies()
            val expected = null

            // then
            assertThat(actual, equalTo(expected))
            coVerify { moviesAPIMock.getMovies(1) }
        }

    @Test
    fun `given MarvelAPI getSimilarMovies(movieId) throws error, when loadMovieDetails(movieId) is invoked, then assert results and verify interactions`() =
        runTest {
            // given
            val movieId = 1234L
            coEvery { moviesAPIMock.getSimilarMovies(movieId = movieId) } throws Exception()

            // when
            val actual = moviesRemoteSourceImpl.loadMovieDetails(movieId)
            val expected = null

            // then
            assertThat(actual, equalTo(expected))
            coVerify { moviesAPIMock.getSimilarMovies(movieId = movieId) }
        }

    @Test
    fun `given MarvelAPI getReviews(movieId) throws error, when loadMovieDetails(movieId) is invoked, then assert results and verify interactions`() =
        runTest {
            // given
            val movieId = 1234L
            coEvery { moviesAPIMock.getSimilarMovies(movieId = movieId) } returns MoviesResponse(
                listOf(mockRMovie1(), mockRMovie2())
            )
            coEvery { moviesAPIMock.getReviews(movieId = movieId) } throws Exception()

            // when
            val actual = moviesRemoteSourceImpl.loadMovieDetails(movieId)
            val expected = null

            // then
            assertThat(actual, equalTo(expected))
            coVerifyAll {
                moviesAPIMock.getSimilarMovies(movieId = movieId)
                moviesAPIMock.getReviews(movieId = movieId)
            }
        }

    @Test
    fun `given MarvelAPI getMovieDetails(movieId) throws error, when loadMovieDetails(movieId) is invoked, then assert results and verify interactions`() =
        runTest {
            // given
            val movieId = 1234L
            coEvery { moviesAPIMock.getSimilarMovies(movieId = movieId) } returns MoviesResponse(
                listOf(mockRMovie1(), mockRMovie2())
            )
            coEvery { moviesAPIMock.getReviews(movieId = movieId) } returns ReviewsResponse(
                emptyList()
            )
            coEvery { moviesAPIMock.getMovieDetails(movieId = movieId) } throws Exception()

            // when
            val actual = moviesRemoteSourceImpl.loadMovieDetails(movieId)
            val expected = null

            // then
            assertThat(actual, equalTo(expected))
            coVerifyAll {
                moviesAPIMock.getSimilarMovies(movieId = movieId)
                moviesAPIMock.getReviews(movieId = movieId)
                moviesAPIMock.getMovieDetails(movieId = movieId)
            }
        }

    @Test
    fun `given all requests are successfully done, when loadMovieDetails(movieId) is invoked, then assert results and verify interactions`() =
        runTest {
            // given
            val movieId = 123456L
            val mockMovieDetailsResponse = mockMovieDetailsResponse()
            coEvery { moviesAPIMock.getSimilarMovies(movieId = movieId) } returns MoviesResponse(
                listOf(mockRMovie1(), mockRMovie2())
            )
            coEvery { moviesAPIMock.getReviews(movieId = movieId) } returns ReviewsResponse(
                emptyList()
            )
            coEvery { moviesAPIMock.getMovieDetails(movieId = movieId) } returns mockMovieDetailsResponse()

            // when
            val actual = moviesRemoteSourceImpl.loadMovieDetails(movieId)
            val expected = DMovieDetails(
                movieId = movieId,
                title = mockMovieDetailsResponse.title,
                releaseDate = formatStringDateToLocalDate(mockMovieDetailsResponse.release_date),
                rating = mockMovieDetailsResponse.vote_average,
                imageUrl = baseUrl + mockMovieDetailsResponse.backdrop_path,
                genres = emptyList(),
                runtime = formatRDurationToDuration(mockMovieDetailsResponse.runtime),
                description = mockMovieDetailsResponse.overview,
                reviews = emptyList(),
                similarMovies = listOf(expectedSimilarDMovie1(), expectedSimilarDMovie2())
            )

            // then
            assertThat(actual, equalTo(expected))
            coVerifyAll {
                moviesAPIMock.getSimilarMovies(movieId = movieId)
                moviesAPIMock.getReviews(movieId = movieId)
                moviesAPIMock.getMovieDetails(movieId = movieId)
            }
        }

    private fun mockRMovie1() = RMovie(
        id = 1234,
        title = "title",
        release_date = "2024-12-12",
        vote_average = 5.0f,
        backdrop_path = "backdrop_path",
        poster_path = "poster_path,"
    )

    private fun mockRMovie2() = RMovie(
        id = 12345,
        title = "title2",
        release_date = "2025-12-12",
        vote_average = 10.0f,
        backdrop_path = "backdrop_path2",
        poster_path = "poster_path2,"
    )

    private fun mockMovieDetailsResponse() = MovieDetailsResponse(
        id = 123456,
        title = "title3",
        release_date = "2026-12-12",
        vote_average = 8.0f,
        backdrop_path = "backdrop_path3",
        runtime = 90,
        overview = "overview",
        genres = emptyList(),
    )

    private fun expectedDMovie1(): DMovie {
        val rMovie = mockRMovie1()
        return rMovie.mapToDomain(baseUrl + rMovie.backdrop_path)
    }

    private fun expectedDMovie2(): DMovie {
        val rMovie = mockRMovie2()
        return rMovie.mapToDomain(baseUrl + rMovie.backdrop_path)
    }

    private fun expectedSimilarDMovie1(): DMovie {
        val rMovie = mockRMovie1()
        return rMovie.mapToDomain(baseUrl + rMovie.poster_path)
    }

    private fun expectedSimilarDMovie2(): DMovie {
        val rMovie = mockRMovie2()
        return rMovie.mapToDomain(baseUrl + rMovie.poster_path)
    }

}