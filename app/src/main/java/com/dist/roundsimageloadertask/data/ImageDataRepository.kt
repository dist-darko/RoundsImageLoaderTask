package com.dist.roundsimageloadertask.data

import com.dist.roundsimageloadertask.base.Failure
import com.dist.roundsimageloadertask.base.NetworkResponse
import com.dist.roundsimageloadertask.base.Response
import com.dist.roundsimageloadertask.base.parseResponse
import com.dist.roundsimageloadertask.data.api.ImageDetailsResponse
import com.dist.roundsimageloadertask.data.api.ImageDetailsService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class ImageDataRepository(
    private val retrofit: Retrofit
) : ImageRepository {

    override suspend fun getAll(): Response<Failure, List<ImageDetailsResponse>> {
        val response = try {
            createGetImageApi().getAllImageDetails()
                .parseResponse()
        } catch (e: Exception) {
            return Response.failure(Failure.ServerError)
        }

        return when(response) {
            is NetworkResponse.SuccessResponse -> Response.success(response.value)
            is NetworkResponse.EmptyBodySuccessResponse -> Response.failure(Failure.ServerError)
            is NetworkResponse.ErrorResponse -> Response.failure(Failure.ServerError)
        }
    }

    private fun createGetImageApi(): ImageDetailsService {
        return retrofit.create(ImageDetailsService::class.java)
    }
}