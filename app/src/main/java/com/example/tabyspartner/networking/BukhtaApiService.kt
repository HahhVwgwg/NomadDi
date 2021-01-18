package com.example.tabyspartner.networking

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


private const val BASE_URL = "https://api.interpaysys.com/v1/svc/card_withdrawal/"

interface BukhtaApiService {
    //    @FormUrlEncoded
    @POST("requests/calculate_fee")
    fun calculateFee(@Body request: FeeRequest): Call<BukhtaFeeResponse>

    @POST("requests")
    fun withdrawCash(@Body request: FeeRequest): Call<BukhtaWithDrawResponse>
}

object BukhtaApi {
    private val client = OkHttpClient.Builder().apply {
        addInterceptor(BukhtaApiInterceptor())
    }.build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .baseUrl(BASE_URL)
        .build()

    val retrofitService : BukhtaApiService by lazy {
        retrofit.create(BukhtaApiService::class.java)
    }
}

class BukhtaApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization","Bearer RXUb0Ga7-A2Ea3EgbSsbuTT_GqiUHPiA8J2vlh-HYT8.FivNX5fU4ZRppTyOl3VIO-VBo31t97KcH2HLTByc07U")
            .addHeader("Service-Key","1")
            .build()
        return chain.proceed(request)
    }
}