package com.manosprojects.themoviedb.domain.source.local

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.core.content.edit
import com.google.gson.Gson
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.domain.source.local.data.LMovie
import com.manosprojects.themoviedb.sharedpref.SharedPrefKeys
import com.manosprojects.themoviedb.utils.formatLocalDateToLDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import javax.inject.Inject

interface MoviesLocalSource {
    fun getMovies(): Flow<List<DMovie>>
    fun isMovieFavourite(movieId: Long): Boolean
    fun setFavourite(movieId: Long, isFavourite: Boolean)
    fun storeMovies(movies: List<DMovie>)
}

class MoviesLocalSourceImpL @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context,
) : MoviesLocalSource {
    override fun getMovies(): Flow<List<DMovie>> {
        return flowOf(emptyList())
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
        val movieIds = mutableSetOf<String>()
        movies.forEach {
            val dataFile = "${it.movieId}-data"
            val imageFile = "${it.movieId}-image.png"
            val image = it.image
            image?.let { storeImage(imageFile = imageFile, image = image) }
            storeLMovieToFile(dataFile = dataFile, lMovie = it.mapToLocal())
            movieIds.add(it.movieId.toString())
        }
        sharedPreferences.edit {
            putStringSet(SharedPrefKeys.MOVIE_IDS, movieIds)
        }
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

}