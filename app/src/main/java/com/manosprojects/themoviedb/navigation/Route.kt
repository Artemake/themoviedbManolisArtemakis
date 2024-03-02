package com.manosprojects.themoviedb.navigation

sealed class Route(val destination: String) {
    data object HomeRoute : Route("home")
    data class MovieDetails(
        private val movieId: String
    ) : Route("$movieBaseDestination/$movieId") {
        companion object {
            private const val movieBaseDestination = "moviedetails/"
            private const val movieIdKey = "movieId"

            fun getBaseDestination(): String {
                return "$movieBaseDestination/$movieIdKey"
            }
        }
    }
}