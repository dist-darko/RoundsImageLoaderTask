package com.dist.image_loader_lib.image_loader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageDownloader {

    suspend fun downloadImage(imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val networkUrl = imageUrl.toURL()

            var connection: HttpURLConnection? = null
            var inputStream: InputStream? = null

            try {
                connection = networkUrl.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.inputStream
                    return@withContext BitmapFactory.decodeStream(inputStream)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Close the InputStream and HttpURLConnection
                try {
                    inputStream?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                connection?.disconnect()
            }

            return@withContext null
        }
    }

    private fun String.toURL(): URL = URL(this)
}