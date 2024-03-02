package com.manosprojects.themoviedb.features.home.domain.source.remote.api

import com.manosprojects.themoviedb.features.home.domain.source.remote.data.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MoviesAPI {

    @Headers(
        "Accept: application/json",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIwZjIyYzdiMDBjNTdlYTk2N2ZhMTg5ZGFmZDk2MzA3NiIsInN1YiI6IjY0NTM5NDY4ZDQ4Y2VlMDBmY2VkZTY5YSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.S-sRwU7SB8gnT_3RYSC-6Hm48jEP3Hd6eHiHKTz13nA"
    )
    @GET("movie/popular?language=en-US")
    suspend fun getMovies(@Query("page") page: Int): MoviesResponse

}