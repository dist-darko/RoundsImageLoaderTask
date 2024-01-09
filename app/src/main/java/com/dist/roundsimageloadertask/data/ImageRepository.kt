package com.dist.roundsimageloadertask.data

import com.dist.roundsimageloadertask.base.Failure
import com.dist.roundsimageloadertask.base.Response
import com.dist.roundsimageloadertask.data.api.ImageDetailsResponse

interface ImageRepository {
    suspend fun getAll(): Response<Failure, List<ImageDetailsResponse>>
}