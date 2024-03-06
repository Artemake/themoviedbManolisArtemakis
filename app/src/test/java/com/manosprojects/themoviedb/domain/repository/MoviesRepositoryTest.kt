package com.manosprojects.themoviedb.domain.repository

import android.graphics.Bitmap
import app.cash.turbine.test
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.MoviesLocalSource
import com.manosprojects.themoviedb.domain.source.remote.MoviesRemoteSource
import com.manosprojects.themoviedb.network.di.ImageDownloader
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesRepositoryTest {

    private val moviesLocalSourceMock: MoviesLocalSource = mockk()
    private val moviesRemoteSourceMock: MoviesRemoteSource = mockk()
    private val imageDownloaderMock: ImageDownloader = mockk()

    private lateinit var moviesRepositoryImpl: MoviesRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    private val mockDMovie1 = DMovie(
        movieId = 1234,
        title = "movie",
        releaseDate = formatStringDateToLocalDate("2024-12-12"),
        rating = 5.0f,
        imageUrl = "movieurlimage.jpeg",
        isFavourite = false,
    )

    private val mockDMovie2 = DMovie(
        movieId = 12345,
        title = "movie2",
        releaseDate = formatStringDateToLocalDate("2025-12-12"),
        rating = 8.0f,
        imageUrl = "movieu2rlimage.jpeg",
        isFavourite = false,
    )

    private val listMockDMovies = listOf(mockDMovie1, mockDMovie2)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        initialiseMoviesRepository()
    }

    @Test
    fun `given movies are stored locally, when subscribed to getInitialMovies(), then assert results and verify interactions`() =
        runTest {
            // given
            every { moviesLocalSourceMock.areMoviesStored() } returns true
            every { moviesLocalSourceMock.getMovies() } returns listMockDMovies
            every { moviesRemoteSourceMock.incrementPage() } just runs

            // when
            moviesRepositoryImpl.getInitialMovies().test {
                // then
                with(awaitItem()) {
                    assertThat(this, equalTo(listMockDMovies))
                }
                verifyAll {
                    moviesLocalSourceMock.areMoviesStored()
                    moviesLocalSourceMock.getMovies()
                    moviesRemoteSourceMock.incrementPage()
                }
                awaitComplete()
            }
        }

    @Test
    fun `given movies are not stored locally and movies are retrieved from remote, when subscribed to getInitialMovies(), then assert results and verify interactions`() =
        runTest {
            // given
            val mock1Bitmap: Bitmap = mockk()
            val mock2Bitmap: Bitmap = mockk()
            coEvery { imageDownloaderMock.downloadImage(mockDMovie1.imageUrl) } returns mock1Bitmap
            coEvery { imageDownloaderMock.downloadImage(mockDMovie2.imageUrl) } returns mock2Bitmap
            every { moviesLocalSourceMock.areMoviesStored() } returns false
            coEvery { moviesRemoteSourceMock.loadMovies() } returns listMockDMovies
            every { moviesLocalSourceMock.storeMovies(any()) } just runs

            // when
            moviesRepositoryImpl.getInitialMovies().test {
                // then
                with(awaitItem()) {
                    assertThat(this, equalTo(listMockDMovies))
                }

                coVerify {
                    imageDownloaderMock.downloadImage(mockDMovie1.imageUrl)
                    imageDownloaderMock.downloadImage(mockDMovie2.imageUrl)
                }
                verify {
                    moviesLocalSourceMock.storeMovies(
                        listOf(
                            mockDMovie1 to mock1Bitmap,
                            mockDMovie2 to mock2Bitmap
                        )
                    )
                }
                awaitComplete()
            }
        }

    @Test
    fun `given movies are retrieved from remote, when loadMovies() is invoked, then assert results and verify interactions`() =
        runTest {
            // given
            coEvery { moviesRemoteSourceMock.loadMovies() } returns listMockDMovies
            every { moviesLocalSourceMock.isMovieFavourite(mockDMovie1.movieId) } returns true
            every { moviesLocalSourceMock.isMovieFavourite(mockDMovie2.movieId) } returns true
            // when
            val actual = moviesRepositoryImpl.loadMovies()

            // then
            assertThat(
                actual,
                equalTo(
                    listOf(
                        mockDMovie1.copy(isFavourite = true),
                        mockDMovie2.copy(isFavourite = true)
                    )
                )
            )
            coVerify { moviesRemoteSourceMock.loadMovies() }
        }

    @Test
    fun `given movie details are retrieved from remote, when loadMovieDetails(movieId) is invoked, then assert results and verify interactions`() =
        runTest {
            // given
            coEvery { moviesRemoteSourceMock.loadMovies() } returns null

            // when
            val actual = moviesRepositoryImpl.loadMovies()

            // then
            assertThat(actual, equalTo(null))
            coVerify { moviesRemoteSourceMock.loadMovies() }
        }

    @Test
    fun `when markMovieAsFavourite() is invoked, then assert results and verify interactions`() {
        // given
        val movieId = 123L
        every { moviesLocalSourceMock.setFavourite(movieId, true) } just runs

        // when
        moviesRepositoryImpl.markMovieAsFavourite(movieId, true)

        // then
        verify { moviesLocalSourceMock.setFavourite(movieId, true) }
    }

    private fun initialiseMoviesRepository() {
        moviesRepositoryImpl = MoviesRepositoryImpl(
            moviesLocalSource = moviesLocalSourceMock,
            moviesRemoteSource = moviesRemoteSourceMock,
            imageDownloader = imageDownloaderMock,
        )
    }
}