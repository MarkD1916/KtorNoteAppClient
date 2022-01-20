package com.androiddevs.ktornoteapp.repository.main

import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MainRepository {
    suspend fun insertNote(note: Note)


    fun getAllNotes(): Flow<Resource<List<Note>>>

    suspend fun getNoteById(noteId: String): Note?
}