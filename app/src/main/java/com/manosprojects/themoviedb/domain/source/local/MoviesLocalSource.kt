package com.manosprojects.themoviedb.domain.source.local

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.data.LMovie
import com.manosprojects.themoviedb.domain.source.local.mappers.mapToCache
import com.manosprojects.themoviedb.domain.source.local.mappers.mapToDomain
import com.manosprojects.themoviedb.sharedpref.SharedPrefKeys
import com.manosprojects.themoviedb.utils.loadData
import com.manosprojects.themoviedb.utils.storeDataToFile
import com.manosprojects.themoviedb.utils.storeImage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface MoviesLocalSource {
    fun areMoviesStored(): Boolean
    fun getMovies(): List<DMovie>
    fun isMovieFavourite(movieId: Long): Boolean
    fun setFavourite(movieId: Long, isFavourite: Boolean)
    fun storeMovies(moviesWithImages: List<Pair<DMovie, Bitmap?>>)
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

    override fun getMovies(): List<DMovie> {
        val movieIds = loadMovieIds()
        return if (movieIds.isEmpty()) {
            emptyList()
        } else {
            movieIds.map {
                loadMovieAndMapToDomain(it)
            }
        }.filterNotNull()
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

    override fun storeMovies(moviesWithImages: List<Pair<DMovie, Bitmap?>>) {
        if (moviesWithImages.isEmpty()) return
        val movieIds = mutableListOf<String>()
        moviesWithImages.forEach { movieWithImage ->
            storeDMovie(movieWithImage)
            movieIds.add(movieWithImage.first.movieId.toString())
        }
        storeMovieIds(movieIds = movieIds)
    }

    private fun storeDMovie(movieWithImage: Pair<DMovie, Bitmap?>) {
        val movie = movieWithImage.first
        val image = movieWithImage.second
        val dataFile = movie.movieId.toString() + dataPostfix
        val imageFile = movie.movieId.toString() + imagePostfix
        image?.let { storeImage(imageFile = imageFile, image = image, context = context) }
        storeDataToFile(
            dataFile = dataFile,
            data = movie.mapToCache(context.filesDir.absolutePath + "/" + imageFile),
            context = context
        )
    }

    private fun loadMovieAndMapToDomain(movieId: String): DMovie? {
        val lMovie = loadData<LMovie>(dataFile = movieId + dataPostfix, context = context)
        return lMovie?.mapToDomain()
    }

    private fun storeMovieIds(movieIds: List<String>) {
        val gson = Gson()
        val json = gson.toJson(movieIds)
        sharedPreferences.edit {
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