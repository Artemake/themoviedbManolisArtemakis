package com.manosprojects.themoviedb.features.home.domain.usecase

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import javax.inject.Inject

interface LoadMoviesUC {
    suspend fun execute(): List<DMovie>
}

class LoadMoviesUCImpl @Inject constructor() : LoadMoviesUC {
    override suspend fun execute(): List<DMovie> {
        TODO("Not yet implemented")
    }

}