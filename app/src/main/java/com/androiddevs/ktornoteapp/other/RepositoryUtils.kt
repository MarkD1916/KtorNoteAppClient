package com.androiddevs.ktornoteapp.other

import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import com.vmakd1916gmail.com.login_logout_register.api.Variables
import retrofit2.Response

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: Exception) {
        if (!Variables.isNetworkConnected) {
            Resource.Error("No Internet Connection")
        }
        else {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}

fun getAuthResponseFromServer(response: Response<SimpleResponse>): Response<SimpleResponse> {

    if (response.isSuccessful && response.body()!!.successful) {
        return response
    } else {
        throw Exception(response.body()?.message ?: response.message())
    }
}

fun <T> getNoteResponseFromServer(response: Response<T>): Response<T> {

    if (response.isSuccessful) {
        return response
    } else {
        throw Exception(response.message())
    }
}
