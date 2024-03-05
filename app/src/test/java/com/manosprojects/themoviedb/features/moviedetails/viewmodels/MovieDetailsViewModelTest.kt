package com.manosprojects.themoviedb.features.moviedetails.viewmodels

import app.cash.turbine.test
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.usecase.LoadMovieDetailsUC
import com.manosprojects.themoviedb.features.moviedetails.contract.MovieDetailsContract
import com.manosprojects.themoviedb.features.moviedetails.data.MovieDetailsModel
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    private val loadMovieDetailsUCMock: LoadMovieDetailsUC = mockk()
    private val movieId = 1234L

    private lateinit var movieDetailsViewModel: MovieDetailsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `given LoadMovieDetailsUC will emit a DMovieDetails object, when MovieDetailsViewModel is initialised, then assert results and verify interactions`() =
        runTest {
            // given
            coEvery { loadMovieDetailsUCMock.execute(movieId) } returns mockDMovieDetails()
            movieDetailsViewModel = MovieDetailsViewModel(
                loadMovieDetailsUC = loadMovieDetailsUCMock,
                movieId = movieId
            )
            advanceUntilIdle()

            // when
            movieDetailsViewModel.uiState.test {
                // then
                with(awaitItem()) {
                    assertThat(
                        this,
                        equalTo(MovieDetailsContract.State.Data(expectedMovieDetailsModel()))
                    )
                }
                coVerify { loadMovieDetailsUCMock.execute(movieId) }
            }

        }

    @Test
    fun `given LoadMovieDetailsUC will emit null, when MovieDetailsViewModel is initialised, then assert results and verify interactions`() =
        runTest {
            // given
            coEvery { loadMovieDetailsUCMock.execute(movieId) } returns null
            movieDetailsViewModel = MovieDetailsViewModel(
                loadMovieDetailsUC = loadMovieDetailsUCMock,
                movieId = movieId
            )
            advanceUntilIdle()

            // when
            movieDetailsViewModel.uiState.test {
                // then
                with(awaitItem()) {
                    assertThat(this, equalTo(MovieDetailsContract.State.Initial))
                }
                coVerify { loadMovieDetailsUCMock.execute(movieId) }
            }

            movieDetailsViewModel.effect.test {
                // then
                with(awaitItem()) {
                    assertThat(this, equalTo(MovieDetailsContract.ErrorEffect))
                }
            }
        }

    private fun mockDMovieDetails() = DMovieDetails(
        movieId = movieId,
        title = "title",
        releaseDate = formatStringDateToLocalDate("2024-12-12"),
        rating = 5.0f,
        imageUrl = "imageUrl",
        isFavourite = false,
        genres = emptyList(),
        runtime = 90.toDuration(DurationUnit.MINUTES),
        description = "description",
        reviews = emptyList(),
        similarMovies = emptyList(),
    )

    private fun expectedMovieDetailsModel() = MovieDetailsModel(
        movieId = movieId,
        title = "title",
        releaseDate = "12 December 2024",
        rating = 5.0f,
        imageUrl = "imageUrl",
        isFavourite = false,
        genres = emptyList(),
        runtime = "1 h 30 min",
        description = "description",
        reviews = emptyList(),
        similarMovies = emptyList(),
    )

}