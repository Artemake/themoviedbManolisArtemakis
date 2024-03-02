package com.manosprojects.themoviedb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.manosprojects.themoviedb.features.home.views.HomeScreen
import com.manosprojects.themoviedb.features.moviedetails.views.MovieDetailsScreen

@Composable
fun MovieDBNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Route.HomeRoute.destination,
    ) {
        composable(route = Route.HomeRoute.destination) {
            HomeScreen(
                navigateToMovie = navController::navigateToMovieDetails,
            )
        }

        val movieIdKey = "movieId"
        composable(route = Route.MovieDetails.getBaseDestination()) {
            val movieId = it.arguments?.getString(movieIdKey)
            movieId?.let {
                MovieDetailsScreen(
                    movieId = movieId,
                    onBackButtonPressed = navController::popBackStack
                )
            }
        }

    }
}

fun NavController.navigateToMovieDetails(movieId: String) {
    navigate(Route.MovieDetails(movieId = movieId).destination)
}