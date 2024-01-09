package com.dist.roundsimageloadertask.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dist.image_loader_lib.image_loader.ImageLoader
import com.dist.roundsimageloadertask.ImageData
import com.dist.roundsimageloadertask.R

class ImageListAdapter(
    private val imageLoader: ImageLoader,
    private val images: MutableList<ImageData> = ArrayList()
) : RecyclerView.Adapter<ImageListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        return ImageListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false), imageLoader)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    fun addAll(images: List<ImageData>) {
        this.images.clear()
        this.images.addAll(images)
        notifyDataSetChanged()
    }

    fun removeAll() {
        this.images.clear()
        notifyDataSetChanged()
    }
}