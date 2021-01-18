package com.dataplus.tabyspartner.networking

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


private const val BASE_URL = "https://api.mobizon.kz/service/message/"



interface MobizonApiService {
//    @FormUrlEncoded
    @POST("sendsmsmessage")
    fun sendMessage(
            @Query("recipient") recipient : String,
            @Query("text") text : String,
            @Query("apiKey") apiKey : String
    ): Call<MobizonResponse>
}

object MobizonApi {
    private val client = OkHttpClient.Builder().apply {
        addInterceptor(MobizonApiInterceptor())
    }.build()

    private val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(BASE_URL)
            .build()
    val retrofitService : MobizonApiService by lazy {
        retrofit.create(MobizonApiService::class.java)
    }
}

class MobizonApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
                .newBuilder()
                .addHeader("cache-control","no-cache")
                .addHeader("content-type","application/x-www-form-urlencoded")
                .build()
        return chain.proceed(request)
    }
}