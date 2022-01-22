package com.androiddevs.ktornoteapp.ui.notemodification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.other.Event
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import com.androiddevs.ktornoteapp.repository.main.MainRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ModificationNoteViewModel @Inject constructor(
    private val repository: MainRepositoryImpl
) : ViewModel() {

    private val _note = MutableLiveData<Event<Resource<Note>>>()

    val note: LiveData<Event<Resource<Note>>> = _note

    fun insertNote(note: Note) =
        GlobalScope.launch { //in case coroutine should live all application live
            repository.insertNote(note)
        }

    fun getNoteById(id: String) {
        _note.postValue(Event(Resource.Loading(null)))
        viewModelScope.launch(Dispatchers.IO) {
            val note = repository.getNoteById(id)
            note?.let {
                _note.postValue(Event(Resource.Success(it)))
                return@launch
            }
            _note.postValue(Event(Resource.Error("Note not found", null)))
        }
    }
}