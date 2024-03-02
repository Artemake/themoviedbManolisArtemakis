package com.manosprojects.themoviedb.features.home.domain.repository

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import com.manosprojects.themoviedb.features.home.domain.source.local.MoviesLocalSource
import com.manosprojects.themoviedb.features.home.domain.source.remote.MoviesRemoteSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface MoviesRepository {
    suspend fun getInitialMovies(): Flow<List<DMovie>?>
    suspend fun loadMovies(): List<DMovie>?
}

class MoviesRepositoryImpl @Inject constructor(
    private val moviesLocalSource: MoviesLocalSource,
    private val moviesRemoteSource: MoviesRemoteSource,
) : MoviesRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getInitialMovies(): Flow<List<DMovie>?> {
        return moviesLocalSource.getMovies().flatMapLatest {
            if (it.isNotEmpty()) {
                flowOf(it)
            } else {
                flowOf(moviesRemoteSource.loadMovies())
            }
        }
    }

    override suspend fun loadMovies(): List<DMovie>? {
        return moviesRemoteSource.loadMovies()
    }

}