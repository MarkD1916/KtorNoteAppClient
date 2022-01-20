package com.androiddevs.ktornoteapp.repository.main

import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun getAllNotes(): Flow<Resource<List<Note>>>
}