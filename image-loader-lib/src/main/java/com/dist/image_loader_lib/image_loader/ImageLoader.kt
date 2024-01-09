package com.dist.image_loader_lib.image_loader

import android.widget.ImageView

interface ImageLoader {
    fun loadImage(imageUrl: String, placeholder: Int, imageView: ImageView)
}