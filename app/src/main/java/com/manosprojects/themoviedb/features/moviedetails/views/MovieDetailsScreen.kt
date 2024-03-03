package com.manosprojects.themoviedb.features.moviedetails.views

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.manosprojects.themoviedb.features.moviedetails.viewmodels.MovieDetailsViewModel

@Composable
fun MovieDetailsScreen(
    movieId: Long,
    onBackButtonPressed: () -> Unit,
) {
    val viewModel =
        hiltViewModel<MovieDetailsViewModel, MovieDetailsViewModel.MovieDetailsViewModelFactory> { factory ->
            factory.create(movieId = movieId)
        }
}