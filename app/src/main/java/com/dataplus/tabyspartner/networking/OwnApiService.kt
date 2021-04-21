package com.dataplus.tabyspartner.networking

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://taxi.mir-crm.com/"

interface OwnApiService {
    @GET("api/drivers/news")
    fun getNews(): Call<List<OwnNewsResponse>>

    @GET("api/drivers/ref7")
    fun getIncome(@Query("phone") phone: String): Call<OwnBaseResponse>

    @GET("api/drivers/new-ref")
    fun invite(@Query("phone") phone: String, @Query("ref") ref: String): Call<OwnBaseResponse>
}

object OwnApi {
    private val client = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }.build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .baseUrl(BASE_URL)
        .build()

    val retrofitService : OwnApiService by lazy {
        retrofit.create(OwnApiService::class.java)
    }
}