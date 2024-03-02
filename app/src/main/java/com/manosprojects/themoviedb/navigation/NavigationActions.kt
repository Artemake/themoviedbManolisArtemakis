package com.manosprojects.themoviedb.navigation

import androidx.navigation.NavController


fun NavController.navigateToMovieDetails(movieId: Int) {
    navigate(Route.MovieDetails(movieId = movieId.toString()).destination)
}