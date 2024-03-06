package com.manosprojects.themoviedb.utils

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

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

fun <T> storeDataToFile(dataFile: String, data: T, context: Context) {
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

fun <T> loadData(dataFile: String, context: Context): T? {
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
