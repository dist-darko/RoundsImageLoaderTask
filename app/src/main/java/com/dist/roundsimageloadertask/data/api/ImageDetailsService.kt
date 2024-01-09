package com.dist.roundsimageloadertask.data.api

import retrofit2.Response
import retrofit2.http.GET

interface ImageDetailsService {

    @GET("image_list.json")
    suspend fun getAllImageDetails() : Response<List<ImageDetailsResponse>>
}