package com.dataplus.tabyspartner.networking

import android.util.Log
import com.dataplus.tabyspartner.Application
import com.dataplus.tabyspartner.utils.SharedHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
private const val BASE_PARTNERS_URL = "https://my.nomad-di.kz/"
object APIClient {
    private var retrofit: Retrofit? = null
    val aPIClient: PartnersApiService?
        get() {
            if (retrofit == null) {
                val gson: Gson = GsonBuilder()
                    .setLenient()
                    .create()
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_PARTNERS_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit?.create(PartnersApiService::class.java)
        }
    private val httpClient: OkHttpClient
        get() = try {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            OkHttpClient()
                .newBuilder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
//                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(AddHeaderInterceptor())
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    private class AddHeaderInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder: Request.Builder = chain.request().newBuilder()
            builder.addHeader("X-Requested-With", "XMLHttpRequest")
            builder.addHeader(
                "Authorization",
                SharedHelper.getKey(Application.getInstance(), "access_token", "")
            )
            Log.d(
                "TTT access_token",
                SharedHelper.getKey(Application.getInstance(), "access_token", "")
            )
            return chain.proceed(builder.build())
        }
    }
}