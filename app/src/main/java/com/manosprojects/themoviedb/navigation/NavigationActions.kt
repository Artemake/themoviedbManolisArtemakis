package com.manosprojects.themoviedb.navigation

import androidx.navigation.NavController


fun NavController.navigateToMovieDetails(movieId: Long) {
    navigate(Route.MovieDetails(movieId = movieId).destination)
}