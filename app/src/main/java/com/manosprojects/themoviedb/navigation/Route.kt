package com.manosprojects.themoviedb.navigation

import com.manosprojects.themoviedb.R

sealed class Route(val destination: String) {

    data object HomeRoute : Route("home")

    data class MovieDetails(
        private val movieId: String
    ) : Route("$movieBaseDestination/$movieId") {
        companion object {
            const val movieBaseDestination = "moviedetails/"
            private const val movieIdKey = "movieId"

            fun getBaseDestination(): String {
                return "$movieBaseDestination/$movieIdKey"
            }
        }
    }
}

val topDestinations = listOf(
    Route.HomeRoute
)

fun isTopLevelDestination(destination: String): Boolean {
    return topDestinations.any { it.destination == destination }
}

fun getAppBarTitleResource(destination: String): Int {
    return when {
        destination == Route.HomeRoute.destination -> R.string.home_app_bar_title
        destination.contains(Route.MovieDetails.movieBaseDestination) -> R.string.movie_details_app_bar_title
        else -> error("The destination has not been subscribed to Route file")
    }
}