package com.androiddevs.ktornoteapp.repository.main

import android.util.Log
import com.androiddevs.ktornoteapp.data.local.DAO.NoteDAO
import com.androiddevs.ktornoteapp.data.local.model.LocallyDeletedNoteID
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.data.remote.api.NoteApi
import com.androiddevs.ktornoteapp.data.remote.requests.AccountRequest
import com.androiddevs.ktornoteapp.data.remote.requests.DeleteNoteRequest
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import com.androiddevs.ktornoteapp.other.getAuthResponseFromServer
import com.androiddevs.ktornoteapp.other.getNoteResponseFromServer
import com.androiddevs.ktornoteapp.other.networkBoundResource
import com.androiddevs.ktornoteapp.other.safeCall
import com.vmakd1916gmail.com.login_logout_register.api.Variables
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val noteApi: NoteApi,
    private val noteDao: NoteDAO
) : MainRepository {
    override suspend fun insertNote(note: Note) {
        return withContext(Dispatchers.IO) {
            val response = try {
                noteApi.addNote(note)
            } catch (e: Exception) {
                null
            }
            if (response != null && response.isSuccessful) {
                noteDao.insertNote(note.apply { isSynced = true })
            } else {
                noteDao.insertNote(note)
            }
        }
    }

    override suspend fun insertNotes(notes: List<Note>) {
        notes.forEach { insertNote(it) }
    }

    override suspend fun deleteNote(noteID: String) {
        val response = try{
            noteApi.deleteNote(DeleteNoteRequest(noteID))
        } catch (e: Exception){
            null
        }
        noteDao.deleteNoteById(noteID)
        if (response == null || !response.isSuccessful) {
            noteDao.insertLocallyDeletedNoteID(LocallyDeletedNoteID(noteID))
        } else{
            deleteLocallyDeletedNoteID(noteID)
        }
    }

    override suspend fun deleteLocallyDeletedNoteID(deletedNoteID: String) {
        noteDao.deleteLocallyDeletedNoteID(deletedNoteID)
    }

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
                    insertNotes(it)
                }
            },
            shouldFetch = {
                Variables.isNetworkConnected
            }
        )
    }

    override suspend fun getNoteById(noteId: String): Note? = noteDao.getNoteById(noteId)
}