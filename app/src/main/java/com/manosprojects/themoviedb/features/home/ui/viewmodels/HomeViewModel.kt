package com.manosprojects.themoviedb.features.home.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import com.manosprojects.themoviedb.features.home.domain.usecase.GetMoviesUC
import com.manosprojects.themoviedb.features.home.domain.usecase.LoadMoviesUC
import com.manosprojects.themoviedb.features.home.ui.contract.HomeContract
import com.manosprojects.themoviedb.features.home.ui.data.HomeMovieModel
import com.manosprojects.themoviedb.mvibase.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUC: GetMoviesUC,
    private val loadMoviesUC: LoadMoviesUC,
) :
    BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    private val errorMessage = "Something went wrong with the request"

    init {
        viewModelScope.launch {
            getMoviesUC.execute().collect {
                it?.let {
                    setState {
                        copy(movies = it.map { dMovie -> dMovie.mapToUIModel() })
                    }
                } ?: setEffect {
                    HomeContract.Effect.ShowSnack(message = errorMessage)
                }
            }
        }
    }

    override fun createInitialState() = HomeContract.State(
        showLoading = true,
        movies = emptyList()
    )

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.OnFavoritePressed -> onFavoritePressed(event.movie.movieId)
            is HomeContract.Event.OnMoviePressed -> onMoviePressed(event.movie.movieId)
            is HomeContract.Event.OnScrolledToEnd -> onScrolledToEnd()
        }
    }

    private fun onFavoritePressed(movieId: Int) {

    }

    private fun onMoviePressed(movieId: Int) {
        setEffect {
            HomeContract.Effect.NavigateToMovie(movieId = movieId)
        }
    }

    private fun onScrolledToEnd() {
        setState {
            copy(showLoading = true)
        }
        viewModelScope.launch {
            loadMoviesUC.execute().collect { newMovies ->
                newMovies?.let {
                    setState {
                        val newMoviesList = buildList {
                            addAll(movies)
                            addAll(newMovies.map { dMovie -> dMovie.mapToUIModel() })
                        }
                        copy(showLoading = false, movies = newMoviesList)
                    }
                } ?: setEffect {
                    HomeContract.Effect.ShowSnack(message = errorMessage)
                }
            }
        }
    }

    private fun DMovie.mapToUIModel(): HomeMovieModel {
        return HomeMovieModel(
            title = title,
            movieId = movieId,
            releaseDate = releaseDate,
            rating = rating,
            image = image
        )
    }
}