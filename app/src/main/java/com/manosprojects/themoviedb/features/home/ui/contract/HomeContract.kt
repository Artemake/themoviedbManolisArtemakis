package com.manosprojects.themoviedb.features.home.ui.contract

import com.manosprojects.themoviedb.features.home.ui.data.HomeMovieModel
import com.manosprojects.themoviedb.mvibase.UiEffect
import com.manosprojects.themoviedb.mvibase.UiEvent
import com.manosprojects.themoviedb.mvibase.UiState

data object HomeContract {
    sealed interface State : UiState {
        data object Initial : State
        data class Loaded(val showLoading: Boolean, val movies: List<HomeMovieModel>) : State
    }

    sealed interface Effect : UiEffect {
        data class NavigateToMovie(val movieId: String) : Effect
    }

    sealed interface Event : UiEvent {
        data class OnMoviePressed(val movie: HomeMovieModel) : Event
        data class OnFavoritePressed(val movie: HomeMovieModel) : Event
        data object OnScrolledToEnd : Event
    }
}