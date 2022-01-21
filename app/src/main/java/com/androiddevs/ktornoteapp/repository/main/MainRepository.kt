package com.androiddevs.ktornoteapp.repository.main

import com.androiddevs.ktornoteapp.data.local.model.LocallyDeletedNoteID
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import kotlinx.coroutines.flow.Flow
import java.util.*

interface MainRepository {

    suspend fun insertNote(note: Note)

    suspend fun insertNotes(notes: List<Note>)

    suspend fun deleteLocallyDeletedNoteID(deletedNoteID: String)

    suspend fun deleteNote(noteID: String)

    fun getAllNotes(): Flow<Resource<List<Note>>>

    suspend fun getNoteById(noteId: String): Note?
}