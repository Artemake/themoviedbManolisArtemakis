package com.manosprojects.themoviedb.domain.usecase

import com.manosprojects.themoviedb.domain.repository.MoviesRepository
import javax.inject.Inject

interface MarkMovieAsFavouriteUC {
    fun execute(movieId: Int, isFavourite: Boolean)
}

class MarkMovieAsFavouriteUCImpl @Inject constructor(
    private val moviesRepository: MoviesRepository
) : MarkMovieAsFavouriteUC {
    override fun execute(movieId: Int, isFavourite: Boolean) {
        moviesRepository.markMovieAsFavourite(movieId = movieId, isFavourite = isFavourite)
    }

}