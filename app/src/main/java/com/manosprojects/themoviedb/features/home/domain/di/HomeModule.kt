package com.manosprojects.themoviedb.features.home.domain.di

import com.manosprojects.themoviedb.features.home.domain.usecase.GetMoviesUC
import com.manosprojects.themoviedb.features.home.domain.usecase.GetMoviesUCImp
import com.manosprojects.themoviedb.features.home.domain.usecase.LoadMoviesUC
import com.manosprojects.themoviedb.features.home.domain.usecase.LoadMoviesUCImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface HomeModule {

    @Binds
    fun bindGetMoviesUC(getMoviesUCImpl: GetMoviesUCImp): GetMoviesUC

    @Binds
    fun bindLoadMoviesUC(loadMoviesUCImpl: LoadMoviesUCImpl): LoadMoviesUC
}