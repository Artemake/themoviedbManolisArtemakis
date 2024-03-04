package com.manosprojects.themoviedb.domain.repository

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.data.DomainState
import com.manosprojects.themoviedb.domain.source.local.MoviesLocalSource
import com.manosprojects.themoviedb.domain.source.remote.MoviesRemoteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface MoviesRepository {
    suspend fun getInitialMovies(): Flow<Pair<List<DMovie>?, DomainState>>
    fun loadMovies(): Flow<List<DMovie>?>
    fun loadMovieDetails(movieId: Long): Flow<DMovieDetails?>
    fun markMovieAsFavourite(movieId: Long, isFavourite: Boolean)
}

class MoviesRepositoryImpl @Inject constructor(
    private val moviesLocalSource: MoviesLocalSource,
    private val moviesRemoteSource: MoviesRemoteSource,
) : MoviesRepository {

    override suspend fun getInitialMovies(): Flow<Pair<List<DMovie>?, DomainState>> {
        var dMovies: List<DMovie>? = null
        return if (moviesLocalSource.areMoviesStored()) {
            moviesRemoteSource.incrementPage()
            moviesLocalSource.getMovies().map {
                dMovies = it
                it to DomainState.LOADING
            }.onCompletion {
                val list = dMovies ?: emptyList()
                emit(list to DomainState.LOAD_COMPLETE)
            }
        } else {
            moviesRemoteSource.loadMovies().map { dMoviesList ->
                dMoviesList?.map { dMovie ->
                    dMovie.copy(
                        isFavourite = moviesLocalSource.isMovieFavourite(
                            dMovie.movieId
                        )
                    )
                }.also { dMovies = dMoviesList } to DomainState.LOADING
            }.onCompletion {
                emit(dMovies to DomainState.LOAD_COMPLETE)
                withContext(Dispatchers.IO) {
                    dMovies?.let { list ->
                        if (list.isNotEmpty()) {
                            moviesLocalSource.storeMovies(list)
                        }
                    }
                }
            }
        }
    }

    override fun loadMovies(): Flow<List<DMovie>?> {
        return moviesRemoteSource.loadMovies().map { dMoviesList ->
            dMoviesList?.map { dMovie ->
                dMovie.copy(
                    isFavourite = moviesLocalSource.isMovieFavourite(
                        dMovie.movieId
                    )
                )
            }
        }
    }

    override fun loadMovieDetails(movieId: Long): Flow<DMovieDetails?> {
        return moviesRemoteSource.loadMovieDetails(movieId = movieId).map {
            it?.copy(
                isFavourite = moviesLocalSource.isMovieFavourite(
                    it.movieId
                )
            )
        }
    }

    override fun markMovieAsFavourite(movieId: Long, isFavourite: Boolean) {
        moviesLocalSource.setFavourite(movieId = movieId, isFavourite = isFavourite)
    }

}