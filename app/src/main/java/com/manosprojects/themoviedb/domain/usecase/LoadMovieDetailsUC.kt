package com.manosprojects.themoviedb.domain.usecase

import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LoadMovieDetailsUC {
    fun execute(movieId: Long): Flow<DMovieDetails?>
}

class LoadMovieDetailsUCImpl @Inject constructor(
    private val repository: MoviesRepository
) : LoadMovieDetailsUC {
    override fun execute(movieId: Long): Flow<DMovieDetails?> {
        return repository.loadMovieDetails(movieId = movieId)
    }

}