package com.manosprojects.themoviedb.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

suspend fun loadImage(imageName: String, context: Context): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val file = File(context.filesDir, imageName)
            if (file.exists()) {
                val inputStream = FileInputStream(file)
                BitmapFactory.decodeStream(inputStream)
            } else {
                null
            }
        } catch (e: IOException) {
            null
        }
    }
}

fun storeImage(imageFile: String, image: Bitmap, context: Context) {
    val file = File(context.filesDir, imageFile)
    try {
        val fos = FileOutputStream(file)
        image.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

inline fun <reified T> storeDataToFile(dataFile: String, data: T, context: Context) {
    val gson = Gson()
    val jsonString = gson.toJson(data)

    val file = File(context.filesDir, dataFile)
    try {
        val writer = FileWriter(file)
        writer.use {
            it.write(jsonString)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

inline fun <reified T> loadData(dataFile: String, context: Context): T? {
    val gson = Gson()
    val file = File(context.filesDir, dataFile)
    if (!file.exists()) {
        return null
    }
    return try {
        val reader = FileReader(file)
        val type = object : TypeToken<T>() {}.type
        gson.fromJson(reader, type)
    } catch (e: IOException) {
        null
    }
}
