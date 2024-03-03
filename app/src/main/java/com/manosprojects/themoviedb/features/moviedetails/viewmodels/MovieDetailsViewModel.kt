package com.manosprojects.themoviedb.features.moviedetails.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manosprojects.themoviedb.domain.usecase.LoadMovieDetails
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MovieDetailsViewModel.MovieDetailsViewModelFactory::class)
class MovieDetailsViewModel @AssistedInject constructor(
    @Assisted private val movieId: Long,
    private val loadMovieDetails: LoadMovieDetails,
) : ViewModel() {

    @AssistedFactory
    interface MovieDetailsViewModelFactory {
        fun create(movieId: Long): MovieDetailsViewModel
    }

    init {
        viewModelScope.launch {
            loadMovieDetails.execute(movieId).collect {

            }
        }
    }
}