package com.dist.roundsimageloadertask.presentation

import com.dist.roundsimageloadertask.ImageData
import com.dist.roundsimageloadertask.data.api.ImageDetailsResponse

fun ImageDetailsResponse.toImageData() : ImageData = ImageData(
    id = id,
    imageUrl = url,
    imageName = extractNameFromPath(url)
)

fun List<ImageDetailsResponse>.toImageDetailsPresentationList() : List<ImageData> {
    return map { imageDetailsResponse -> imageDetailsResponse.toImageData() }
}

fun extractNameFromPath(imageUrl: String): String? {
    val parts = imageUrl.split("/")
    val imageName = parts.lastOrNull { it.endsWith(".jpg", ignoreCase = true) }

    return imageName
}