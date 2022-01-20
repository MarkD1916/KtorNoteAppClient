package com.androiddevs.ktornoteapp.repository.main

import com.androiddevs.ktornoteapp.data.local.DAO.NoteDAO
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.data.remote.api.NoteApi
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import com.androiddevs.ktornoteapp.other.networkBoundResource
import com.androiddevs.ktornoteapp.other.safeCall
import com.vmakd1916gmail.com.login_logout_register.api.Variables
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val noteApi: NoteApi,
    private val noteDao: NoteDAO
) : MainRepository {
    override fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                noteApi.getNotes()
            },
            saveFetchResult = { response ->
                response.body()?.let {
                    //ToDo: insert note in db
                }
            },
            shouldFetch = {
                Variables.isNetworkConnected
            }
        )
    }
}