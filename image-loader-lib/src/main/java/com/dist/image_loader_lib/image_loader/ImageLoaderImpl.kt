package com.dist.image_loader_lib.image_loader

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageLoaderImpl private constructor(
    private val scope: CoroutineScope,
    private val context: Context
) : ImageLoader {
    private val imageDownloader: ImageDownloader = ImageDownloader()
    private val imageCache: ImageCacheImpl = ImageCacheImpl(scope, context)

    override fun loadImage(imageUrl: String, placeholder: Int, imageView: ImageView) {
        scope.launch {
            imageView.setImageDrawable(AppCompatResources.getDrawable(context, placeholder))
            if (imageCache.isCached(imageUrl)) {
                val bitmap = withContext(Dispatchers.IO) { imageCache.loadImage(imageUrl) }
                bitmap?.let {
                    launch(Dispatchers.Main) {
                        setImageToView(it, imageView)
                    }
                }
            } else {
                val downloadedImage = withContext(Dispatchers.IO) { imageDownloader.downloadImage(imageUrl) }
                downloadedImage?.let {
                    imageCache.saveImage(imageUrl, it)
                    launch(Dispatchers.Main) {
                        setImageToView(it, imageView)
                    }
                }
            }
        }
    }

    private fun setImageToView(bitmap: Bitmap, imageView: ImageView) {
        imageView.setImageBitmap(bitmap)
    }

    companion object {
        fun getInstance(coroutineScope: CoroutineScope, context: Context) : ImageLoader {
            return ImageLoaderImpl(coroutineScope, context)
        }
    }
}