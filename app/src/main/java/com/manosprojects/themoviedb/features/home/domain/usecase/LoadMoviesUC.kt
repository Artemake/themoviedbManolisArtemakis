package com.manosprojects.themoviedb.features.home.domain.usecase

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import com.manosprojects.themoviedb.features.home.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LoadMoviesUC {
    fun execute(): Flow<List<DMovie>?>
}

class LoadMoviesUCImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : LoadMoviesUC {
    override fun execute(): Flow<List<DMovie>?> {
        return moviesRepository.loadMovies()
    }

}