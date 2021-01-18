package com.dataplus.tabyspartner.networking

import com.dataplus.tabyspartner.tokengenerator.RandomString
import okhttp3.Interceptor
import okhttp3.Response

class YandexApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-Client-ID","taxi/park/2e8584835dd64db99482b4b21f62a2ae")
            .addHeader("X-Api-Key","elogWzVHgKGFFcucmDUnERQsVfytM/OvtfyffQe/")
            .addHeader("X-Idempotency-Token",RandomString().nextString())
            .build()
        return chain.proceed(request)
    }
}