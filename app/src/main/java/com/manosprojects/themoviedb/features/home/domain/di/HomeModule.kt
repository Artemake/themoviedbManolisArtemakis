package com.manosprojects.themoviedb.features.home.domain.di

import com.manosprojects.themoviedb.features.home.domain.repository.MoviesRepository
import com.manosprojects.themoviedb.features.home.domain.repository.MoviesRepositoryImpl
import com.manosprojects.themoviedb.features.home.domain.source.local.MoviesLocalSource
import com.manosprojects.themoviedb.features.home.domain.source.local.MoviesLocalSourceImpL
import com.manosprojects.themoviedb.features.home.domain.source.remote.MoviesRemoteSource
import com.manosprojects.themoviedb.features.home.domain.source.remote.MoviesRemoteSourceImpl
import com.manosprojects.themoviedb.features.home.domain.source.remote.api.MoviesAPI
import com.manosprojects.themoviedb.features.home.domain.usecase.GetMoviesUC
import com.manosprojects.themoviedb.features.home.domain.usecase.GetMoviesUCImp
import com.manosprojects.themoviedb.features.home.domain.usecase.LoadMoviesUC
import com.manosprojects.themoviedb.features.home.domain.usecase.LoadMoviesUCImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface HomeModule {

    @Binds
    fun bindGetMoviesUC(getMoviesUCImpl: GetMoviesUCImp): GetMoviesUC

    @Binds
    fun bindLoadMoviesUC(loadMoviesUCImpl: LoadMoviesUCImpl): LoadMoviesUC

    @Binds
    fun bindMoviesRepository(moviesRepositoryImpl: MoviesRepositoryImpl): MoviesRepository

    @Singleton
    @Binds
    fun bindMoviesLocalSource(moviesLocalSourceImpl: MoviesLocalSourceImpL): MoviesLocalSource

    @Singleton
    @Binds
    fun bindMoviesRemoteSource(moviesRemoteSourceImpl: MoviesRemoteSourceImpl): MoviesRemoteSource
}

@Module
@InstallIn(SingletonComponent::class)
object MoviesAPI {

    // APIs

    @Provides
    @Singleton
    fun provideMoviesAPI(retrofit: Retrofit): MoviesAPI {
        return retrofit.create(MoviesAPI::class.java)
    }

}