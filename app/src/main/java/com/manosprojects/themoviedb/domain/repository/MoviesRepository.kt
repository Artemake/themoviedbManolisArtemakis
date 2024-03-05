package com.manosprojects.themoviedb.domain.repository

import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.data.DMovieDetails
import com.manosprojects.themoviedb.domain.source.local.MoviesLocalSource
import com.manosprojects.themoviedb.domain.source.remote.MoviesRemoteSource
import com.manosprojects.themoviedb.network.di.ImageDownloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface MoviesRepository {
    fun getInitialMovies(): Flow<List<DMovie>?>
    suspend fun loadMovies(): List<DMovie>?
    suspend fun loadMovieDetails(movieId: Long): DMovieDetails?
    fun markMovieAsFavourite(movieId: Long, isFavourite: Boolean)
}

class MoviesRepositoryImpl @Inject constructor(
    private val moviesLocalSource: MoviesLocalSource,
    private val moviesRemoteSource: MoviesRemoteSource,
    private val imageDownloader: ImageDownloader,
) : MoviesRepository {

    override fun getInitialMovies(): Flow<List<DMovie>?> {
        return if (moviesLocalSource.areMoviesStored()) {
            moviesRemoteSource.incrementPage()
            flowOf(moviesLocalSource.getMovies())
        } else {
            flow {
                val movies = moviesRemoteSource.loadMovies()
                emit(movies)
                withContext(Dispatchers.IO) {
                    val moviesWithImages = movies?.map { dMovie ->
                        val image = imageDownloader.downloadImage(dMovie.imageUrl)
                        image?.let { dMovie to it }
                    }?.filterNotNull()
                    moviesWithImages?.let { moviesLocalSource.storeMovies(it) }
                }
            }
        }
    }

    override suspend fun loadMovies(): List<DMovie>? {
        return moviesRemoteSource.loadMovies()?.map { dMovie ->
            dMovie.copy(
                isFavourite = moviesLocalSource.isMovieFavourite(
                    dMovie.movieId
                )
            )
        }
    }

    override suspend fun loadMovieDetails(movieId: Long): DMovieDetails? {
        return moviesRemoteSource.loadMovieDetails(movieId = movieId)?.let {
            it.copy(
                isFavourite = moviesLocalSource.isMovieFavourite(
                    it.movieId
                )
            )
        }
    }

    override fun markMovieAsFavourite(movieId: Long, isFavourite: Boolean) {
        moviesLocalSource.setFavourite(movieId = movieId, isFavourite = isFavourite)
    }

//    private fun loadInitialMoviesFromRemote(): Flow<Pair<List<DMovie>?, DomainState>> {
//        var dMovies: List<DMovie>? = null
//        return moviesRemoteSource.loadMovies().map { dMoviesList ->
//            dMoviesList?.map { dMovie ->
//                dMovie.copy(
//                    isFavourite = moviesLocalSource.isMovieFavourite(
//                        dMovie.movieId
//                    )
//                )
//            }.also { dMovies = dMoviesList } to DomainState.LOADING
//        }.onCompletion {
//            emit(dMovies to DomainState.LOAD_COMPLETE)
//            withContext(Dispatchers.IO) {
//                dMovies?.let { moviesLocalSource.storeMovies(it) }
//            }
//        }
//    }

}