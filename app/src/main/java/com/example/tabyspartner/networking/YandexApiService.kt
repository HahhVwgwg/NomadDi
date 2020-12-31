package com.example.tabyspartner.networking

import com.example.tabyspartner.YandexApiInterceptor
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST


private const val BASE_URL = "https://fleet-api.taxi.yandex.net"

interface YandexApiService {
    @POST("/v1/parks/driver-profiles/list")
    fun getUser(@Body request: GetSomethingRequest): Call<DriverProfilesResponse>


    @POST("/v2/parks/transactions/categories/list")
    fun getCategories(@Body request: CategoryRequest): Call<CategoryResponse>


}

object YandexApi {
    private val client = OkHttpClient.Builder().apply {
        addInterceptor(YandexApiInterceptor())
    }.build()

    private val retrofit = Retrofit.Builder()

        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
            .client(client)
        .baseUrl(BASE_URL)
        .build()

    val retrofitService : YandexApiService by lazy {
        retrofit.create(YandexApiService::class.java)
    }



}