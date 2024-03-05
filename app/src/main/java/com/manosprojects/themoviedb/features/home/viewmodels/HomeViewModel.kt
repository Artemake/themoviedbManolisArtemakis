package com.manosprojects.themoviedb.features.home.viewmodels

import androidx.lifecycle.viewModelScope
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.usecase.GetMoviesUC
import com.manosprojects.themoviedb.domain.usecase.LoadMoviesUC
import com.manosprojects.themoviedb.domain.usecase.MarkMovieAsFavouriteUC
import com.manosprojects.themoviedb.features.home.contract.HomeContract
import com.manosprojects.themoviedb.features.home.data.HomeMovieModel
import com.manosprojects.themoviedb.mvibase.BaseViewModel
import com.manosprojects.themoviedb.utils.formatDomainDateToUIDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUC: GetMoviesUC,
    private val loadMoviesUC: LoadMoviesUC,
    private val markMovieAsFavouriteUC: MarkMovieAsFavouriteUC,
) :
    BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    init {
        viewModelScope.launch {
            getMoviesUC.execute().onCompletion {
                changeOfState(
                    loading = false
                )
            }.collect { movies ->
                if (movies == null) {
                    setErrorEffect()
                }
                changeOfState(
                    newMovies = movies?.map { it.mapToUIModel() } ?: emptyList(),
                    loading = true
                )
            }
        }
    }

    override fun createInitialState() = HomeContract.State(
        showLoading = true,
        refreshing = false,
        movies = emptyList()
    )

    override fun handleEvent(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.OnFavoritePressed -> onFavoritePressed(event.movie)
            is HomeContract.Event.OnMoviePressed -> onMoviePressed(event.movie.movieId)
            is HomeContract.Event.OnScrolledToEnd -> onScrolledToEnd()
            is HomeContract.Event.OnPulledToRefresh -> onPulledToRefresh()
        }
    }

    private fun onPulledToRefresh() {
        setState {
            copy(refreshing = true, movies = emptyList())
        }
        viewModelScope.launch {
            val newMovies = loadMoviesUC.execute()
            if (newMovies == null) {
                setErrorEffect()
            }
            changeOfState(
                newMovies = newMovies?.map { it.mapToUIModel() } ?: emptyList(),
                refreshing = false
            )
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

    private fun onMoviePressed(movieId: Long) {
        setEffect {
            HomeContract.Effect.NavigateToMovie(movieId = movieId)
        }
    }

    private fun onScrolledToEnd() {
        if (uiState.value.showLoading) {
            return
        }
        changeOfState(loading = true)
        viewModelScope.launch {
            val newMovies = loadMoviesUC.execute()
            if (newMovies == null) {
                setErrorEffect()
            }
            changeOfState(
                newMovies = newMovies?.map { it.mapToUIModel() } ?: emptyList(),
                loading = false
            )
        }
    }

    private fun changeOfState(
        newMovies: List<HomeMovieModel> = emptyList(),
        loading: Boolean? = null,
        refreshing: Boolean = false
    ) {
        setState {
            val newSet = buildSet {
                addAll(movies + newMovies)
            }
            val showLoading = loading ?: showLoading
            copy(movies = newSet.toList(), showLoading = showLoading, refreshing = refreshing)
        }
    }

    private fun setErrorEffect() {
        val errorMessage = "Something went wrong with the request"
        setEffect {
            HomeContract.Effect.ShowSnack(message = errorMessage)
        }
    }

    private fun DMovie.mapToUIModel(): HomeMovieModel {
        return HomeMovieModel(
            title = title,
            movieId = movieId,
            releaseDate = formatDomainDateToUIDate(releaseDate),
            rating = rating,
            imageUrl = imageUrl,
            isFavorite = isFavourite,
        )
    }
}