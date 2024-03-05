package com.manosprojects.themoviedb.network.di

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GeneralAPI {

    @Streaming
    @GET
    suspend fun downloadImage(@Url imageUrl: String): ResponseBody
}