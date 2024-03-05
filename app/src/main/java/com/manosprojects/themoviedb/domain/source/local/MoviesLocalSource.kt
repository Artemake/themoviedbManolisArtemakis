package com.manosprojects.themoviedb.domain.source.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.data.LMovie
import com.manosprojects.themoviedb.domain.source.local.mappers.mapToCache
import com.manosprojects.themoviedb.domain.source.local.mappers.mapToDomain
import com.manosprojects.themoviedb.sharedpref.SharedPrefKeys
import com.manosprojects.themoviedb.utils.loadData
import com.manosprojects.themoviedb.utils.loadImage
import com.manosprojects.themoviedb.utils.storeDataToFile
import com.manosprojects.themoviedb.utils.storeImage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface MoviesLocalSource {
    fun areMoviesStored(): Boolean
    fun getMovies(): Flow<List<DMovie>>
    fun isMovieFavourite(movieId: Long): Boolean
    fun setFavourite(movieId: Long, isFavourite: Boolean)
    fun storeMovies(movies: List<DMovie>)
}

class MoviesLocalSourceImpL @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context,
) : MoviesLocalSource {

    private val dataPostfix = "-data"
    private val imagePostfix = "-image.png"

    override fun areMoviesStored(): Boolean {
        return loadMovieIds().isNotEmpty()
    }

    override fun getMovies(): Flow<List<DMovie>> {
        val movieIds = loadMovieIds()
        return if (movieIds.isEmpty()) {
            flowOf(emptyList())
        } else {
            flow {
                val dMovies = mutableListOf<DMovie>()
                movieIds.forEach {
                    val dMovie = loadMovieAndMapToDomain(it)
                    dMovie?.let {
                        dMovies.add(dMovie)
                        emit(dMovies)
                    }
                }
            }
        }
    }

    override fun isMovieFavourite(movieId: Long): Boolean {
        val key = SharedPrefKeys.FAVORITE_PREFIX + movieId
        return sharedPreferences.getBoolean(key, false)
    }

    override fun setFavourite(movieId: Long, isFavourite: Boolean) {
        val key = SharedPrefKeys.FAVORITE_PREFIX + movieId
        sharedPreferences.edit {
            putBoolean(key, isFavourite)
        }
    }

    override fun storeMovies(movies: List<DMovie>) {
        if (movies.isEmpty()) return
        val movieIds = mutableListOf<String>()
        movies.forEach { dMovie ->
            storeDMovie(dMovie)
            movieIds.add(dMovie.movieId.toString())
        }
        storeMovieIds(movieIds = movieIds)
    }

    private fun storeDMovie(movie: DMovie) {
        val dataFile = movie.movieId.toString() + dataPostfix
        val imageFile = movie.movieId.toString() + imagePostfix
        val image = movie.image
        image?.let { storeImage(imageFile = imageFile, image = image, context = context) }
        storeDataToFile(dataFile = dataFile, data = movie.mapToCache(), context = context)
    }

    private suspend fun loadMovieAndMapToDomain(movieId: String): DMovie? {
        val lMovie = loadData<LMovie>(dataFile = movieId + dataPostfix, context = context)
        val image = loadImage(imageName = movieId + imagePostfix, context = context)
        return lMovie?.mapToDomain(image)
    }

    private fun storeMovieIds(movieIds: List<String>) {
        sharedPreferences.edit {
            val gson = Gson()
            val json = gson.toJson(movieIds)
            putString(SharedPrefKeys.MOVIE_IDS, json)
        }
    }

    private fun loadMovieIds(): List<String> {
        val gson = Gson()
        val json = sharedPreferences.getString(SharedPrefKeys.MOVIE_IDS, null)
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}