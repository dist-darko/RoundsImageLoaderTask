package com.dist.roundsimageloadertask.presentation.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dist.image_loader_lib.image_loader.ImageLoader
import com.dist.roundsimageloadertask.ImageData
import com.dist.roundsimageloadertask.R
import com.dist.image_loader_lib.image_loader.ImageLoaderImpl

class ImageListViewHolder(
    itemView: View,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(itemView) {

    private val carImage by lazy { itemView.findViewById<ImageView>(R.id.car_image) }
    private val imageId by lazy { itemView.findViewById<TextView>(R.id.id_of_image) }

    fun bind(model: ImageData) {
        imageLoader.loadImage(model.imageUrl, R.drawable.no_image, carImage)
        imageId.text = model.id.toString()
    }
}