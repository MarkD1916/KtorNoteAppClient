package com.androiddevs.ktornoteapp.repository

import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import com.androiddevs.ktornoteapp.other.Resource
import retrofit2.Response

interface AuthRepository {

    suspend fun register(email: String, password: String): Resource<Response<SimpleResponse>>

    suspend fun login(email: String, password: String): Resource<Response<SimpleResponse>>


}