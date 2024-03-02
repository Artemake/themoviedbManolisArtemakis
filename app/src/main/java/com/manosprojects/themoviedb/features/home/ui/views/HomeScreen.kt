package com.manosprojects.themoviedb.features.home.ui.views

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.manosprojects.themoviedb.features.home.ui.contract.HomeContract
import com.manosprojects.themoviedb.features.home.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navigateToMovie: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = true) {
        homeViewModel.effect.collect {
            when (it) {
                is HomeContract.Effect.NavigateToMovie -> navigateToMovie(it.movieId)
            }
        }
    }

    val state = homeViewModel.uiState.collectAsState().value
    when (state) {
        is HomeContract.State.Initial -> LoadingAnimation()
        is HomeContract.State.Loaded -> {
            LazyColumn {
                items(state.movies) {
                    MovieComponent(
                        title = it.title,
                        releaseDate = it.releaseDate,
                        rating = it.rating.toString(),
                        onFavouritePressed = {
                            homeViewModel.setEvent(
                                HomeContract.Event.OnFavoritePressed(it)
                            )
                        }, onMoviePressed = {
                            homeViewModel.setEvent(
                                HomeContract.Event.OnMoviePressed(it)
                            )
                        })
                }
                item {
                    if (state.showLoading) {
                        LoadingAnimation()
                    }
                }
            }
        }
    }

}

@Composable
private fun LoadingAnimation() {

}
