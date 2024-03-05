package com.manosprojects.themoviedb.domain.source.remote.api

import com.manosprojects.themoviedb.domain.source.remote.data.MovieDetailsResponse
import com.manosprojects.themoviedb.domain.source.remote.data.MoviesResponse
import com.manosprojects.themoviedb.domain.source.remote.data.ReviewsResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

private const val bearerToken: String =
    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIwZjIyYzdiMDBjNTdlYTk2N2ZhMTg5ZGFmZDk2MzA3NiIsInN1YiI6IjY0NTM5NDY4ZDQ4Y2VlMDBmY2VkZTY5YSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.S-sRwU7SB8gnT_3RYSC-6Hm48jEP3Hd6eHiHKTz13nA"


interface MoviesAPI {

    @Headers(
        "Accept: application/json",
        "Authorization: $bearerToken"
    )
    @GET("movie/popular")
    suspend fun getMovies(@Query("page") page: Int): MoviesResponse

    @Headers(
        "Accept: application/json",
        "Authorization: $bearerToken"
    )
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Long): MovieDetailsResponse

    @Headers(
        "Accept: application/json",
        "Authorization: $bearerToken"
    )
    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(@Path("movie_id") movieId: Long): MoviesResponse

    @Headers(
        "Accept: application/json",
        "Authorization: $bearerToken"
    )
    @GET("movie/{movie_id}/reviews")
    suspend fun getReviews(@Path("movie_id") movieId: Long): ReviewsResponse
}