package com.manosprojects.themoviedb.features.home.ui.contract

import com.manosprojects.themoviedb.features.home.ui.data.HomeMovieModel
import com.manosprojects.themoviedb.mvibase.UiEffect
import com.manosprojects.themoviedb.mvibase.UiEvent
import com.manosprojects.themoviedb.mvibase.UiState

data object HomeContract {

    data class State(val showLoading: Boolean, val movies: List<HomeMovieModel>) : UiState

    sealed interface Effect : UiEffect {
        data class NavigateToMovie(val movieId: String) : Effect
        data class ShowSnack(val message: String) : Effect
    }

    sealed interface Event : UiEvent {
        data class OnMoviePressed(val movie: HomeMovieModel) : Event
        data class OnFavoritePressed(val movie: HomeMovieModel) : Event
        data object OnScrolledToEnd : Event
    }
}