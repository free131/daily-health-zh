package com.apoorvdarshan.calorietracker.services

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/** Not an HTTP header: this marker never leaves the process. Only AI adapters may use it. */
internal object AiApiRequest

internal class ApiOnlyNetworkPolicy : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.tag(AiApiRequest::class.java) !== AiApiRequest || request.method != "POST") {
            throw IOException("此版本仅允许你配置的 AI API 请求。")
        }
        return chain.proceed(request)
    }
}
