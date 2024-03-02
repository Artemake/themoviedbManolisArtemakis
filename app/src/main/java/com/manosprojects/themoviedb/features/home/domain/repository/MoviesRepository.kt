package com.manosprojects.themoviedb.features.home.domain.repository

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    suspend fun getInitialMovies(): Flow<List<DMovie>?>
    suspend fun loadMovies(): List<DMovie>?
}