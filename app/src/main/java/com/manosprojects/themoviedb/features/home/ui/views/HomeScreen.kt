package com.manosprojects.themoviedb.features.home.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.manosprojects.themoviedb.features.home.ui.contract.HomeContract
import com.manosprojects.themoviedb.features.home.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navigateToMovie: (String) -> Unit,
    homeMovieModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = true) {
        homeMovieModel.effect.collect {
            when (it) {
                is HomeContract.Effect.NavigateToMovie -> navigateToMovie(it.movieId)
            }
        }
    }

    when (homeMovieModel.uiState.collectAsState().value) {
        is HomeContract.State.Initial -> LoadingAnimation()
        is HomeContract.State.Loaded -> {}
    }

}

@Composable
private fun LoadingAnimation() {

}