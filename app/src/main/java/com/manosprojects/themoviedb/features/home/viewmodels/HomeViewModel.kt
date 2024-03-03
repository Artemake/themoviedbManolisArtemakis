package com.manosprojects.themoviedb.features.home.viewmodels

import androidx.lifecycle.viewModelScope
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.usecase.GetMoviesUC
import com.manosprojects.themoviedb.domain.usecase.LoadMoviesUC
import com.manosprojects.themoviedb.domain.usecase.MarkMovieAsFavouriteUC
import com.manosprojects.themoviedb.features.home.contract.HomeContract
import com.manosprojects.themoviedb.features.home.data.HomeMovieModel
import com.manosprojects.themoviedb.mvibase.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUC: GetMoviesUC,
    private val loadMoviesUC: LoadMoviesUC,
    private val markMovieAsFavouriteUC: MarkMovieAsFavouriteUC
) :
    BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    private val errorMessage = "Something went wrong with the request"

    init {
        viewModelScope.launch {
            val deferred = async {
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
            deferred.await()
            setState {
                copy(showLoading = false)
            }
        }
    }

    override fun createInitialState() = HomeContract.State(
        showLoading = true,
        movies = emptyList()
    )

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.OnFavoritePressed -> onFavoritePressed(event.movie)
            is HomeContract.Event.OnMoviePressed -> onMoviePressed(event.movie.movieId)
            is HomeContract.Event.OnScrolledToEnd -> onScrolledToEnd()
        }
    }

    private fun onFavoritePressed(movie: HomeMovieModel) {
        markMovieAsFavouriteUC.execute(movieId = movie.movieId, isFavourite = !movie.isFavorite)
        setState {
            val newMovies = movies.map {
                if (it.movieId != movie.movieId) {
                    it
                } else {
                    it.copy(isFavorite = !it.isFavorite)
                }
            }
            copy(movies = newMovies)
        }
    }

    private fun onMoviePressed(movieId: Int) {
        setEffect {
            HomeContract.Effect.NavigateToMovie(movieId = movieId)
        }
    }

    private fun onScrolledToEnd() {
        if (uiState.value.showLoading) {
            return
        }
        setState {
            copy(showLoading = true)
        }
        viewModelScope.launch {
            val deferred = async {
                loadMoviesUC.execute().collect { newMovies ->
                    newMovies?.let {
                        setState {
                            val newMoviesList = buildList {
                                addAll(movies)
                                newMovies.forEach { dMovie ->
                                    if (!movies.any { it.movieId == dMovie.movieId }) {
                                        add(dMovie.mapToUIModel())
                                    }
                                }
                            }
                            copy(movies = newMoviesList)
                        }
                    } ?: setEffect {
                        HomeContract.Effect.ShowSnack(message = errorMessage)
                    }
                }
            }
            deferred.await()
            setState {
                copy(showLoading = false)
            }
        }
    }

    private fun DMovie.mapToUIModel(): HomeMovieModel {
        return HomeMovieModel(
            title = title,
            movieId = movieId,
            releaseDate = releaseDate,
            rating = rating,
            image = image,
            isFavorite = isFavourite,
        )
    }
}