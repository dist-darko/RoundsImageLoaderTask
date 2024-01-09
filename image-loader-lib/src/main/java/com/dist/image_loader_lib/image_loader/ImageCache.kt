package com.dist.image_loader_lib.image_loader

import android.graphics.Bitmap

interface ImageCache {
    fun isCached(imageUrl: String) : Boolean
    fun saveImage(imageUrl: String, bitmap: Bitmap) : Boolean
    fun loadImage(imageUrl: String) : Bitmap?
    fun invalidateCache()
}