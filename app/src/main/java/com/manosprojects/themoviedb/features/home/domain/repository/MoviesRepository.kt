package com.manosprojects.themoviedb.features.home.domain.repository

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import com.manosprojects.themoviedb.features.home.domain.source.local.MoviesLocalSource
import com.manosprojects.themoviedb.features.home.domain.source.remote.MoviesRemoteSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface MoviesRepository {
    fun getInitialMovies(): Flow<List<DMovie>?>
    fun loadMovies(): Flow<List<DMovie>?>
    fun markMovieAsFavourite(movieId: Int, isFavourite: Boolean)
}

class MoviesRepositoryImpl @Inject constructor(
    private val moviesLocalSource: MoviesLocalSource,
    private val moviesRemoteSource: MoviesRemoteSource,
) : MoviesRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getInitialMovies(): Flow<List<DMovie>?> {
        return moviesLocalSource.getMovies().flatMapLatest {
            if (it.isNotEmpty()) {
                flowOf(it)
            } else {
                moviesRemoteSource.loadMovies().map { dMoviesList ->
                    dMoviesList?.map { dMovie ->
                        dMovie.copy(
                            isFavourite = moviesLocalSource.isMovieFavourite(
                                dMovie.movieId
                            )
                        )
                    }
                }
            }
        }
    }

    override fun loadMovies(): Flow<List<DMovie>?> {
        return moviesRemoteSource.loadMovies()
    }

    override fun markMovieAsFavourite(movieId: Int, isFavourite: Boolean) {
        moviesLocalSource.setFavourite(movieId = movieId, isFavourite = isFavourite)
    }

}