package com.manosprojects.themoviedb.features.home.viewmodels

import app.cash.turbine.test
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.usecase.GetMoviesUC
import com.manosprojects.themoviedb.domain.usecase.LoadMoviesUC
import com.manosprojects.themoviedb.domain.usecase.MarkMovieAsFavouriteUC
import com.manosprojects.themoviedb.features.home.contract.HomeContract
import com.manosprojects.themoviedb.features.home.mappers.mapToUIModel
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val getMoviesUCMock: GetMoviesUC = mockk()
    private val loadMoviesUCMock: LoadMoviesUC = mockk()
    private val markMovieAsFavouriteUCMock: MarkMovieAsFavouriteUC = mockk(relaxUnitFun = true)

    private lateinit var homeViewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val errorMessage = "Something went wrong with the request"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `given GetMoviesUC will emit a list of DMovie objects, when HomeViewModel is initialised, then assert results and verify interactions`() =
        runTest {
            // given
            every { getMoviesUCMock.execute() } returns flowOf(mockListDMovies())
            initialiseViewModel()
            advanceUntilIdle()

            // when
            homeViewModel.uiState.test {
                // then
                with(awaitItem()) {
                    assertThat(
                        this, equalTo(
                            HomeContract.State(
                                showLoading = false,
                                refreshing = false,
                                movies = mockListDMovies().map { it.mapToUIModel() }
                            )
                        )
                    )
                }
            }
        }

    @Test
    fun `given LoadMovieDetailsUC will emit null, when HomeViewModel is initialised, then assert results and verify interactions`() =
        runTest {
            // given
            every { getMoviesUCMock.execute() } returns flowOf(null)
            initialiseViewModel()
            advanceUntilIdle()

            // when
            homeViewModel.uiState.test {
                with(awaitItem()) {
                    assertThat(
                        this, equalTo(
                            HomeContract.State(
                                showLoading = false,
                                refreshing = false,
                                movies = emptyList()
                            )
                        )
                    )
                }
            }
        }

    @Test
    fun `when handleEvent is invoked with OnFavoritePressed, then assert interactions`() = runTest {
        // given
        val mockDMovie = mockDMovie()
        every { getMoviesUCMock.execute() } returns flowOf(listOf(mockDMovie))
        initialiseViewModel()
        advanceUntilIdle()

        // when
        homeViewModel.handleEvent(HomeContract.Event.OnFavoritePressed(mockDMovie.mapToUIModel()))

        // then
        verify {
            markMovieAsFavouriteUCMock.execute(
                movieId = mockDMovie.movieId,
                isFavourite = !mockDMovie.isFavourite
            )
        }

        val expected = HomeContract.State(
            showLoading = false, refreshing = false, listOf(
                mockDMovie.copy(isFavourite = !mockDMovie.isFavourite).mapToUIModel()
            )
        )
        homeViewModel.uiState.test {
            with(awaitItem()) {
                assertThat(
                    this, equalTo(expected)
                )
            }
        }

    }

    @Test
    fun `when handleEvent is invoked with OnMoviePressed, then assert interactions and verify results`() =
        runTest {
            // given
            val mockDMovie = mockDMovie()
            val mockHomeMovieModel = mockDMovie.mapToUIModel()
            initialiseViewModel()
            every { getMoviesUCMock.execute() } returns flowOf(listOf(mockDMovie))
            advanceUntilIdle()

            // when
            homeViewModel.handleEvent(HomeContract.Event.OnMoviePressed(mockHomeMovieModel))

            // then
            homeViewModel.effect.test {
                with(awaitItem()) {
                    assertThat(
                        this,
                        equalTo(HomeContract.Effect.NavigateToMovie(movieId = mockHomeMovieModel.movieId))
                    )
                }
            }
        }

    @Test
    fun `given LoadDataUC will emit a list of DMovie objects, when handleEvent is invoked with OnScrolledToEnd, then assert interactions and verify results`() =
        runTest {
            // given
            val mockDMovie = mockDMovie()
            initialiseViewModel()
            every { getMoviesUCMock.execute() } returns flowOf(listOf(mockDMovie))
            coEvery { loadMoviesUCMock.execute() } returns mockListDMovies()
            advanceUntilIdle()

            // when
            homeViewModel.handleEvent(HomeContract.Event.OnScrolledToEnd)
            advanceUntilIdle()

            // then
            homeViewModel.uiState.test {
                with(awaitItem()) {
                    assertThat(
                        this, equalTo(
                            HomeContract.State(
                                showLoading = false,
                                refreshing = false,
                                movies = mockListDMovies().map { it.mapToUIModel() }
                            )
                        )
                    )
                }
            }
            coVerify { loadMoviesUCMock.execute() }
        }

    @Test
    fun `given LoadDataUC will emit null, when handleEvent is invoked with OnScrolledToEnd, then assert interactions and verify results`() =
        runTest {
            // given
            val mockDMovie = mockDMovie()
            initialiseViewModel()
            every { getMoviesUCMock.execute() } returns flowOf(listOf(mockDMovie))
            coEvery { loadMoviesUCMock.execute() } returns null
            advanceUntilIdle()

            // when
            homeViewModel.handleEvent(HomeContract.Event.OnScrolledToEnd)
            advanceUntilIdle()

            // then
            homeViewModel.uiState.test {
                with(awaitItem()) {
                    assertThat(
                        this, equalTo(
                            HomeContract.State(
                                showLoading = false,
                                refreshing = false,
                                movies = listOf(mockDMovie.mapToUIModel())
                            )
                        )
                    )
                }
            }
            homeViewModel.effect.test {
                with(awaitItem()) {
                    assertThat(this, equalTo(HomeContract.Effect.ShowSnack(message = errorMessage)))
                }
            }
            coVerify { loadMoviesUCMock.execute() }
        }


    @Test
    fun `given it is not loading and LoadMoviesUC will emit a list of DMovie objects, when handleEvent is invoked with OnPulledToRefresh, then assert interactions and verify results`() =
        runTest {
            // given
            val mockDMovie = mockDMovie()
            initialiseViewModel()
            every { getMoviesUCMock.execute() } returns flowOf(listOf(mockDMovie))
            coEvery { loadMoviesUCMock.execute() } returns mockListDMovies()
            advanceUntilIdle()

            // when
            homeViewModel.handleEvent(HomeContract.Event.OnPulledToRefresh)
            advanceUntilIdle()

            // then
            homeViewModel.uiState.test {
                with(awaitItem()) {
                    assertThat(
                        this, equalTo(
                            HomeContract.State(
                                showLoading = false,
                                refreshing = false,
                                movies = mockListDMovies().map { it.mapToUIModel() }
                            )
                        )
                    )
                }
            }
            coVerify { loadMoviesUCMock.execute() }
        }

    @Test
    fun `given it is not loading and LoadMoviesUC will emit null, when handleEvent is invoked with OnPulledToRefresh, then assert interactions and verify results`() =
        runTest {
            // given
            val mockDMovie = mockDMovie()
            initialiseViewModel()
            every { getMoviesUCMock.execute() } returns flowOf(listOf(mockDMovie))
            coEvery { loadMoviesUCMock.execute() } returns null
            advanceUntilIdle()

            // when
            homeViewModel.handleEvent(HomeContract.Event.OnPulledToRefresh)
            advanceUntilIdle()

            // then
            homeViewModel.uiState.test {
                with(awaitItem()) {
                    assertThat(
                        this, equalTo(
                            HomeContract.State(
                                showLoading = false,
                                refreshing = false,
                                movies = emptyList()
                            )
                        )
                    )
                }
            }
            homeViewModel.effect.test {
                with(awaitItem()) {
                    assertThat(this, equalTo(HomeContract.Effect.ShowSnack(message = errorMessage)))
                }
            }
            coVerify { loadMoviesUCMock.execute() }
        }

    private fun initialiseViewModel() {
        homeViewModel = HomeViewModel(
            getMoviesUC = getMoviesUCMock,
            loadMoviesUC = loadMoviesUCMock,
            markMovieAsFavouriteUC = markMovieAsFavouriteUCMock,
        )
    }

    private fun mockListDMovies() = listOf(
        mockDMovie(),
        DMovie(
            movieId = 12345,
            title = "movie2",
            releaseDate = formatStringDateToLocalDate("2025-12-12"),
            rating = 8.0f,
            imageUrl = "movieu2rlimage.jpeg",
            isFavourite = false,
        )
    )

    private fun mockDMovie() = DMovie(
        movieId = 1234,
        title = "movie",
        releaseDate = formatStringDateToLocalDate("2024-12-12"),
        rating = 5.0f,
        imageUrl = "movieurlimage.jpeg",
        isFavourite = false,
    )

}