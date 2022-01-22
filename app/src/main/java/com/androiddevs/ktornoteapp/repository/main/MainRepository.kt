package com.androiddevs.ktornoteapp.repository.main

import androidx.lifecycle.LiveData
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface MainRepository {

    suspend fun insertNote(note: Note)

    suspend fun insertNotes(notes: List<Note>)

    suspend fun deleteLocallyDeletedNoteID(deletedNoteID: String)

    suspend fun deleteNote(noteID: String)

    fun getAllNotes(): Flow<Resource<List<Note>>>

    suspend fun getNoteById(noteId: String): Note?

    suspend fun syncNotes()

    fun observeNoteByID(noteID: String): LiveData<Note>

    suspend fun addOwnerToNote(
        owner: String,
        noteID: String
    ): Resource<Response<SimpleResponse>>
}