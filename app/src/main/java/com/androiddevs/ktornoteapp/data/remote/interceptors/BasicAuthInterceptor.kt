package com.androiddevs.ktornoteapp.data.remote.interceptors

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.androiddevs.ktornoteapp.other.Constants.IGNORE_AUTH_URL
import com.androiddevs.ktornoteapp.preferences.BasicAuthPreferences
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BasicAuthInterceptor@Inject constructor(
    private val sharedPreferences: BasicAuthPreferences
) : Interceptor {

    var email = sharedPreferences.getStoredEmail()
    var password = sharedPreferences.getStoredPassword()

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