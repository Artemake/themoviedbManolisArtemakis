package com.manosprojects.themoviedb.features.home.domain.usecase

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import com.manosprojects.themoviedb.features.home.domain.repository.MoviesRepository
import javax.inject.Inject

interface LoadMoviesUC {
    suspend fun execute(): List<DMovie>?
}

class LoadMoviesUCImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : LoadMoviesUC {
    override suspend fun execute(): List<DMovie>? {
        return moviesRepository.loadMovies()
    }

}