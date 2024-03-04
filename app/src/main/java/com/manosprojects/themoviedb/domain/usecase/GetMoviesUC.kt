package com.manosprojects.themoviedb.domain.usecase

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.data.DomainState
import com.manosprojects.themoviedb.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetMoviesUC {
    suspend fun execute(): Flow<Pair<List<DMovie>?, DomainState>>
}

class GetMoviesUCImp @Inject constructor(
    private val moviesRepository: MoviesRepository
) : GetMoviesUC {
    override suspend fun execute(): Flow<Pair<List<DMovie>?, DomainState>> {
        return moviesRepository.getInitialMovies()
    }

}