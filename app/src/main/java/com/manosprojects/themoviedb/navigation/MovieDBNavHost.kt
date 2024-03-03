package com.manosprojects.themoviedb.navigation

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.manosprojects.themoviedb.R
import com.manosprojects.themoviedb.features.home.views.HomeScreen
import com.manosprojects.themoviedb.features.moviedetails.views.MovieDetailsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDBNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            val currentDestination = navController.currentBackStackEntryAsState().value?.destination
            currentDestination?.route?.let {
                val shouldShowBackArrow = !isTopLevelDestination(it)
                TopAppBar(
                    title = { Text(text = stringResource(id = getAppBarTitleResource(it))) },
                    navigationIcon = {
                        if (shouldShowBackArrow) {
                            IconButton(onClick = navController::popBackStack) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                    contentDescription = null
                                )
                            }
                        }
                    })
            }
        }
    ) {
        NavHost(
            navController = navController,
            modifier = Modifier.padding(
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding(),
                start = it.calculateStartPadding(LayoutDirection.Ltr),
                end = it.calculateEndPadding(LayoutDirection.Ltr),
            ),
            startDestination = Route.HomeRoute.destination,
        ) {
            composable(route = Route.HomeRoute.destination) {
                HomeScreen(
                    navigateToMovie = navController::navigateToMovieDetails,
                )
            }

            val movieIdKey = Route.MovieDetails.movieIdKey
            composable(
                route = Route.MovieDetails.getBaseDestination(),
                arguments = listOf(navArgument(movieIdKey) { type = NavType.LongType })
            ) { navBaskStackEntry ->
                val movieId = navBaskStackEntry.arguments?.getLong(movieIdKey)
                movieId?.let {
                    MovieDetailsScreen(
                        movieId = movieId,
                        onBackButtonPressed = navController::popBackStack
                    )
                }
            }

        }
    }
}