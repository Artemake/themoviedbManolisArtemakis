package com.manosprojects.themoviedb.features.moviedetails.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.usecase.LoadMovieDetailsUC
import com.manosprojects.themoviedb.features.moviedetails.contract.MovieDetailsContract
import com.manosprojects.themoviedb.features.moviedetails.data.MovieDetailsModel
import com.manosprojects.themoviedb.features.moviedetails.data.MovieDetailsReviewModel
import com.manosprojects.themoviedb.utils.formatDomainDateToUIDate
import com.manosprojects.themoviedb.utils.formatDurationToUITime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MovieDetailsViewModel.MovieDetailsViewModelFactory::class)
class MovieDetailsViewModel @AssistedInject constructor(
    @Assisted private val movieId: Long,
    private val loadMovieDetailsUC: LoadMovieDetailsUC,
) : ViewModel() {

    @AssistedFactory
    interface MovieDetailsViewModelFactory {
        fun create(movieId: Long): MovieDetailsViewModel
    }

    private val _uiState: MutableStateFlow<MovieDetailsContract.State> =
        MutableStateFlow(MovieDetailsContract.State.Initial)
    val uiState = _uiState.asStateFlow()

    private val _effect: Channel<MovieDetailsContract.ErrorEffect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            loadMovieDetailsUC.execute(movieId).collect {
                it?.apply {
                    _uiState.value = MovieDetailsContract.State.Data(mapToUIModel())
                } ?: _effect.send(MovieDetailsContract.ErrorEffect)
            }
        }
    }

    private fun DMovieDetails.mapToUIModel(): MovieDetailsModel {
        return MovieDetailsModel(
            movieId = movieId,
            title = title,
            releaseDate = formatDomainDateToUIDate(releaseDate),
            rating = rating,
            imageUrl = imageUrl,
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
            similarMovies = similarMovies.map { it.imageUrl }.take(6),
        )
    }
}