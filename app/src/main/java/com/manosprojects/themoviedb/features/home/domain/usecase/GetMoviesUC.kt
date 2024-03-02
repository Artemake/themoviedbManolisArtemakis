package com.manosprojects.themoviedb.features.home.domain.usecase

import com.manosprojects.themoviedb.features.home.domain.data.DMovie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetMoviesUC {
    suspend fun execute(): Flow<List<DMovie>>
}

class GetMoviesUCImp @Inject constructor() : GetMoviesUC {
    override suspend fun execute(): Flow<List<DMovie>> {
        TODO("Not yet implemented")
    }

}