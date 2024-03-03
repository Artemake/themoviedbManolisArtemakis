package com.manosprojects.themoviedb.domain.usecase

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetMoviesUC {
    suspend fun execute(): Flow<List<DMovie>?>
}

class GetMoviesUCImp @Inject constructor(
    private val moviesRepository: MoviesRepository
) : GetMoviesUC {
    override suspend fun execute(): Flow<List<DMovie>?> {
        return moviesRepository.getInitialMovies()
    }

}