package com.dist.roundsimageloadertask.base

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun provideRetrofitClient(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://zipoapps-storage-test.nyc3.digitaloceanspaces.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}