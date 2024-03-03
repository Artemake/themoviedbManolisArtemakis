package com.manosprojects.themoviedb.domain.source.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.manosprojects.themoviedb.domain.data.DMovie
import com.manosprojects.themoviedb.sharedpref.SharedPrefKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface MoviesLocalSource {
    fun getMovies(): Flow<List<DMovie>>
    fun isMovieFavourite(movieId: Long): Boolean
    fun setFavourite(movieId: Long, isFavourite: Boolean)
}

class MoviesLocalSourceImpL @Inject constructor(
    private val sharedPreferences: SharedPreferences
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

}