package com.manosprojects.themoviedb.features.moviedetails.contract

import com.manosprojects.themoviedb.features.moviedetails.data.MovieDetailsModel

object MovieDetailsContract {

    sealed interface State {
        data object Initial : State
        data class Data(val movieDetails: MovieDetailsModel) : State
    }

    data object ErrorEffect
}