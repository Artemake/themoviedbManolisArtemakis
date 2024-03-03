package com.manosprojects.themoviedb.domain.usecase

import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LoadMovieDetails {
    fun execute(movieId: Long): Flow<DMovieDetails?>
}

class LoadMovieDetailsImpl @Inject constructor(
    private val repository: MoviesRepository
) : LoadMovieDetails {
    override fun execute(movieId: Long): Flow<DMovieDetails?> {
        return repository.loadMovieDetails(movieId = movieId)
    }

}