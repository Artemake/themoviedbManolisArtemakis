package com.manosprojects.themoviedb.features.home.ui.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.manosprojects.themoviedb.features.home.ui.contract.HomeContract
import com.manosprojects.themoviedb.features.home.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navigateToMovie: (Int) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        homeViewModel.effect.collect {
            when (it) {
                is HomeContract.Effect.NavigateToMovie -> navigateToMovie(it.movieId)
                is HomeContract.Effect.ShowSnack -> showToast(context, it.message)
            }
        }
    }

    val state = homeViewModel.uiState.collectAsState().value

    LazyColumn {
        items(state.movies.size) {
            if (it == state.movies.size - 1) {
                homeViewModel.setEvent(HomeContract.Event.OnScrolledToEnd)
            }
            val item = state.movies[it]
            MovieComponent(
                title = item.title,
                releaseDate = item.releaseDate,
                rating = item.rating.toString(),
                image = item.image,
                onFavouritePressed = {
                    homeViewModel.setEvent(
                        HomeContract.Event.OnFavoritePressed(item)
                    )
                }, onMoviePressed = {
                    homeViewModel.setEvent(
                        HomeContract.Event.OnMoviePressed(item)
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

@Composable
private fun LoadingAnimation() {

}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}