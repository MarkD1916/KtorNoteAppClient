package com.androiddevs.ktornoteapp.repository.auth

import com.androiddevs.ktornoteapp.data.remote.api.NoteApi
import com.androiddevs.ktornoteapp.data.remote.requests.AccountRequest
import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import com.androiddevs.ktornoteapp.other.getAuthResponseFromServer
import com.androiddevs.ktornoteapp.other.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val noteApi: NoteApi
) : AuthRepository {
    override suspend fun register(
        email: String,
        password: String
    ): Resource<Response<SimpleResponse>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val response = noteApi.register(AccountRequest(email, password))
                val result = getAuthResponseFromServer(response)
                Resource.Success(result)
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Resource<Response<SimpleResponse>> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val response = noteApi.login(AccountRequest(email, password))
                val result = getAuthResponseFromServer(response)
                Resource.Success(result)
            }
        }
    }

}