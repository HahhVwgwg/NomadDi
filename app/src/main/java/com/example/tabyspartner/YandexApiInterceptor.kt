package com.example.tabyspartner

import okhttp3.Interceptor
import okhttp3.Response

class YandexApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-Client-ID","taxi/park/2e8584835dd64db99482b4b21f62a2ae")
            .addHeader("X-Api-Key","rdYDkzjrBFfXcfKdem+g//nimmhWWPIn")
            .build()
        return chain.proceed(request)
    }
}