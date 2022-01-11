package com.androiddevs.ktornoteapp.data.remote.interceptors

import com.androiddevs.ktornoteapp.other.Constants.IGNORE_AUTH_URL
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    val email: String? = null
    val password: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.encodedPath in IGNORE_AUTH_URL) {
            return chain.proceed(request)
        }
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", Credentials.basic(email ?: "", password ?: ""))
            .build()
        return chain.proceed(authenticatedRequest)
    }
}