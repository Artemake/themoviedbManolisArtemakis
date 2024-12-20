package com.manosprojects.themoviedb.features.home.contract

import com.manosprojects.themoviedb.features.home.data.HomeMovieModel
import com.manosprojects.themoviedb.mvibase.UiEffect
import com.manosprojects.themoviedb.mvibase.UiEvent
import com.manosprojects.themoviedb.mvibase.UiState

data object HomeContract {

    data class State(
        val showLoading: Boolean,
        val refreshing: Boolean,
        val movies: List<HomeMovieModel>
    ) : UiState

    sealed interface Effect : UiEffect {
        data class NavigateToMovie(val movieId: Long) : Effect
        data class ShowSnack(val message: String) : Effect
    }

    sealed interface Event : UiEvent {
        data class OnMoviePressed(val movie: HomeMovieModel) : Event
        data class OnFavoritePressed(val movie: HomeMovieModel) : Event
        data object OnScrolledToEnd : Event
        data object OnPulledToRefresh : Event
    }
}