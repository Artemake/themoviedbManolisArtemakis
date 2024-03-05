package com.manosprojects.themoviedb.features.home.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.manosprojects.themoviedb.features.home.contract.HomeContract
import com.manosprojects.themoviedb.features.home.data.HomeMovieModel
import com.manosprojects.themoviedb.features.home.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navigateToMovie: (Long) -> Unit,
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
                homeMovieModel = item,
                onFavouritePressed = { homeMovieModel ->
                    homeViewModel.setEvent(
                        HomeContract.Event.OnFavoritePressed(homeMovieModel)
                    )
                },
                onMoviePressed = { homeMovieModel ->
                    homeViewModel.setEvent(
                        HomeContract.Event.OnMoviePressed(homeMovieModel)
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
private fun MovieComponent(
    homeMovieModel: HomeMovieModel,
    onFavouritePressed: (HomeMovieModel) -> Unit,
    onMoviePressed: (HomeMovieModel) -> Unit,
) {
    MovieComponent(
        title = homeMovieModel.title,
        releaseDate = homeMovieModel.releaseDate,
        rating = homeMovieModel.rating,
        image = homeMovieModel.image,
        isFavourite = homeMovieModel.isFavorite,
        onFavouritePressed = {
            onFavouritePressed(homeMovieModel)
        }, onMoviePressed = {
            onMoviePressed(homeMovieModel)
        }
    )
}

@Composable
private fun LoadingAnimation() {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}