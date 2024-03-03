package com.manosprojects.themoviedb.features.moviedetails.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.usecase.LoadMovieDetails
import com.manosprojects.themoviedb.features.moviedetails.contract.MovieDetailsContract
import com.manosprojects.themoviedb.features.moviedetails.data.MovieDetailsModel
import com.manosprojects.themoviedb.features.moviedetails.data.MovieDetailsReviewModel
import com.manosprojects.themoviedb.utils.formatDomainDateToUIDate
import com.manosprojects.themoviedb.utils.formatDurationToUITime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _uiState: MutableStateFlow<MovieDetailsContract.State> =
        MutableStateFlow(MovieDetailsContract.State.Initial)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadMovieDetails.execute(movieId).collect {
                it?.apply {
                    _uiState.value = MovieDetailsContract.State.Data(mapToUIModel())
                }
            }
        }
    }

    private fun DMovieDetails.mapToUIModel(): MovieDetailsModel {
        return MovieDetailsModel(
            movieId = movieId,
            title = title,
            releaseDate = formatDomainDateToUIDate(releaseDate),
            rating = rating,
            image = image,
            isFavourite = isFavourite,
            genres = genres,
            runtime = formatDurationToUITime(runtime),
            description = description,
            reviews = reviews.map {
                MovieDetailsReviewModel(
                    author = it.author,
                    content = it.content
                )
            }.take(3),
            similarMovies = similarMovies.map { it.image }.filterNotNull().take(6),
        )
    }
}