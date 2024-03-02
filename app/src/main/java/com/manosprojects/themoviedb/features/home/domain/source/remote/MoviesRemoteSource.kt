package com.manosprojects.themoviedb.features.home.domain.source.remote

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import javax.inject.Inject

interface MoviesRemoteSource {
    suspend fun loadMovies(): List<DMovie>
}

class MoviesRemoteSourceImpl @Inject constructor() : MoviesRemoteSource {
    override suspend fun loadMovies(): List<DMovie> {
        TODO("Not yet implemented")
    }

}