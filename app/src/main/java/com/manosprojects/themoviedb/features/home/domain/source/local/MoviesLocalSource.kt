package com.manosprojects.themoviedb.features.home.domain.source.local

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface MoviesLocalSource {
    fun getMovies(): Flow<List<DMovie>>
}

class MoviesLocalSourceImpL @Inject constructor() : MoviesLocalSource {
    override fun getMovies(): Flow<List<DMovie>> {
        return flowOf(emptyList())
    }

}