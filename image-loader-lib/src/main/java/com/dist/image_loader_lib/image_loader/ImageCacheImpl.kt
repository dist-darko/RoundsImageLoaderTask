package com.dist.image_loader_lib.image_loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

private typealias HashId = String

class ImageCacheImpl(private val scope: CoroutineScope, private val context: Context) : ImageCache {
    private val cache: MutableMap<HashId, File> = HashMap<HashId, File>()
    private val cacheDir: File = initializeCacheDirectory(context)

    init {
        fetchCachedIfAny(context)
    }

    override fun isCached(imageUrl: String) : Boolean {
        return (cache.containsKey(imageUrl.toSHA256()) && System.currentTimeMillis() - cache.getValue(imageUrl.toSHA256()).lastModified() < CACHE_DURATION_TIME)
    }

    override fun saveImage(imageUrl: String, bitmap: Bitmap): Boolean {
        val imageShaHash = imageUrl.toSHA256()
        val imageFile = File(cacheDir, imageShaHash)
        try {
            imageFile.outputStream().use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
            cache[imageShaHash] = imageFile
        } catch (e: IOException) {
            e.printStackTrace()
            //TODO handle exception if needed do cleanup
            return false
        }
        return true
    }

    override fun loadImage(imageUrl: String): Bitmap? {
        val key = imageUrl.toSHA256()
        if (cache.containsKey(key)) {
            return BitmapFactory.decodeFile(cache.getValue(key).path)
        }
        return null
    }

    private fun initializeCacheDirectory(context: Context): File {
        val appRootDir = context.cacheDir
        val cacheDirectory = File(appRootDir, CACHE_DIR_NAME)

        if (!cacheDirectory.exists()) {
            val directoryCreated = cacheDirectory.mkdirs()
            if (!directoryCreated) {
                //TODO log directory not created error
            }
        }

        return cacheDirectory
    }

    private fun fetchCachedIfAny(context: Context) {
        val cacheDirectory = File(context.cacheDir, CACHE_DIR_NAME)

        if (cacheDirectory.exists() && cacheDirectory.isDirectory) {
            val cachedFiles = cacheDirectory.listFiles()

            cachedFiles?.forEach { file ->
                if (file.isFile) {
                    cache[file.name] = file
                }
            }
        }
    }

    override fun invalidateCache() {
        scope.launch {
            val cacheDirectory = File(context.cacheDir, CACHE_DIR_NAME)
            if (cacheDirectory.exists() && cacheDirectory.isDirectory) {
                val cachedFiles = cacheDirectory.listFiles()

                cachedFiles?.forEach { file ->
                    file.delete()
                }
            }
        }
    }

    private fun String.toSHA256(): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(this.toByteArray())
        return bytes.toHex()
    }

    private fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val CACHE_DIR_NAME = "rounds-image-cache"
        private val CACHE_DURATION_TIME: Long = TimeUnit.HOURS.toMillis(4)
    }
}