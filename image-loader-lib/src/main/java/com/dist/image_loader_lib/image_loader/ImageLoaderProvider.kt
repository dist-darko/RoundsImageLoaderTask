package com.dist.image_loader_lib.image_loader

import android.content.Context
import kotlinx.coroutines.CoroutineScope

class ImageLoaderProvider private constructor() {

    companion object {
        private var imageLoader: ImageLoader? = null

        fun get(coroutineScope: CoroutineScope, context: Context): ImageLoader {
            return imageLoader ?: createImageLoaderInstance(coroutineScope, context)
        }

        private fun createImageLoaderInstance(coroutineScope: CoroutineScope, context: Context): ImageLoader {
            synchronized(this) {
                return imageLoader ?: ImageLoaderImpl.getInstance(coroutineScope, context).also {
                    imageLoader = it
                }
            }
        }
    }
}