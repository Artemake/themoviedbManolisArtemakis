package com.manosprojects.themoviedb.network.di

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import javax.inject.Inject

interface ImageDownloader {
    suspend fun downloadImage(imageUrl: String): Bitmap?
}

class ImageDownloaderImpl @Inject constructor(
    private val generalAPI: GeneralAPI
) : ImageDownloader {
    override suspend fun downloadImage(imageUrl: String): Bitmap? {
        return try {
            val responseBody = generalAPI.downloadImage(imageUrl)
            val stream = responseBody.byteStream()
            BitmapFactory.decodeStream(stream)
        } catch (e: Exception) {
            null
        }
    }

}