package com.manosprojects.themoviedb.domain.usecase

import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.repository.MoviesRepository
import javax.inject.Inject

interface LoadMovieDetailsUC {
    suspend fun execute(movieId: Long): DMovieDetails?
}

class LoadMovieDetailsUCImpl @Inject constructor(
    private val repository: MoviesRepository
) : LoadMovieDetailsUC {
    override suspend fun execute(movieId: Long): DMovieDetails? {
        return repository.loadMovieDetails(movieId = movieId)
    }

}