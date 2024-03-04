package com.manosprojects.themoviedb.domain.source.local

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.data.LMovie
import com.manosprojects.themoviedb.sharedpref.SharedPrefKeys
import com.manosprojects.themoviedb.utils.formatLocalDateToLDate
import com.manosprojects.themoviedb.utils.formatStringDateToLocalDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
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
                    val lMovie = loadLMovie(it + dataPostfix)
                    val image = loadImage(it + imagePostfix)
                    lMovie?.mapToDomain(image)?.let { dMovie ->
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
        val movieIds = mutableListOf<String>()
        movies.forEach { dmovie ->
            val dataFile = dmovie.movieId.toString() + dataPostfix
            val imageFile = dmovie.movieId.toString() + imagePostfix
            val image = dmovie.image
            image?.let { storeImage(imageFile = imageFile, image = image) }
            storeLMovieToFile(dataFile = dataFile, lMovie = dmovie.mapToLocal())
            movieIds.add(dmovie.movieId.toString())
        }
        storeMovieIds(movieIds = movieIds)
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

    private fun storeLMovieToFile(dataFile: String, lMovie: LMovie) {
        val gson = Gson()
        val jsonString = gson.toJson(lMovie)

        val file = File(context.filesDir, dataFile)
        try {
            val writer = FileWriter(file)
            writer.use {
                it.write(jsonString)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadLMovie(dataFile: String): LMovie? {
        val gson = Gson()
        val file = File(context.filesDir, dataFile)
        if (!file.exists()) {
            return null
        }
        return try {
            val reader = FileReader(file)
            val type = object : TypeToken<LMovie>() {}.type
            gson.fromJson(reader, type)
        } catch (e: IOException) {
            null
        }
    }

    suspend fun loadImage(imageName: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, imageName)
                if (file.exists()) {
                    val inputStream = FileInputStream(file)
                    BitmapFactory.decodeStream(inputStream)
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
        }
    }

    private fun storeImage(imageFile: String, image: Bitmap) {
        val file = File(context.filesDir, imageFile)
        try {
            val fos = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun DMovie.mapToLocal(): LMovie {
        return LMovie(
            movieId = movieId,
            title = title,
            releaseDate = formatLocalDateToLDate(releaseDate),
            rating = rating,
            isFavourite = isFavourite,
        )
    }

    private fun LMovie.mapToDomain(image: Bitmap?): DMovie {
        return DMovie(
            movieId = movieId,
            title = title,
            releaseDate = formatStringDateToLocalDate(releaseDate),
            rating = rating,
            image = image,
            isFavourite = isFavourite,
        )
    }

}