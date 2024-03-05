package com.manosprojects.themoviedb.features.home.viewmodels

import kotlinx.coroutines.test.runTest
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun `given GetMoviesUC will emit a list of DMovie objects, when HomeViewModel is initialised, then assert results and verify interactions`() =
        runTest {

        }

    @Test
    fun `given LoadMovieDetailsUC will emit null, when HomeViewModel is initialised, then assert results and verify interactions`() =
        runTest {

        }

    @Test
    fun `test createInitialState()`() {

    }

    @Test
    fun `when handleEvent is invoked with OnFavoritePressed, then assert interactions`() {

    }

    @Test
    fun `when handleEvent is invoked with OnMoviePressed, then assert interactions and verify results`() {

    }

    @Test
    fun `given it is loading, when handleEvent is invoked with OnScrolledToEnd, then assert interactions and verify results`() {

    }

    @Test
    fun `given it is not loading and LoadMoviesUC will emit a list of DMovie objects, when handleEvent is invoked with OnScrolledToEnd, then assert interactions and verify results`() {

    }

    @Test
    fun `given it is not loading and LoadMoviesUC will emit null, when handleEvent is invoked with OnScrolledToEnd, then assert interactions and verify results`() {

    }
}