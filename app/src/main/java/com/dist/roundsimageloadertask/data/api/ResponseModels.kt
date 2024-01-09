package com.dist.roundsimageloadertask.data.api

import com.dist.roundsimageloadertask.ImageData
import com.google.gson.annotations.SerializedName

data class ImageDetailsResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageUrl")
    val url: String
)

