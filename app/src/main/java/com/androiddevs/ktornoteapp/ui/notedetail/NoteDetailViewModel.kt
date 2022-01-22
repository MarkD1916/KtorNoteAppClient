package com.androiddevs.ktornoteapp.ui.notedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import com.androiddevs.ktornoteapp.other.Event
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import com.androiddevs.ktornoteapp.repository.main.MainRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val repository: MainRepositoryImpl
) : ViewModel() {

    private val _addOwnerStatus = MutableLiveData<Event<Resource<SimpleResponse>>>()
    val addOwnerStatus: LiveData<Event<Resource<SimpleResponse>>> = _addOwnerStatus

    fun addOwnerToNote(owner: String, noteID: String) {
        _addOwnerStatus.postValue(Event(Resource.Loading()))
        if (owner.isEmpty() || noteID.isEmpty()) {
            _addOwnerStatus.postValue(Event(Resource.Error("The owner can't be empty")))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.addOwnerToNote(owner, noteID)
            response.data?.body()?.let {
                _addOwnerStatus.postValue(Event(Resource.Success(it)))
                return@launch
            }
            _addOwnerStatus.postValue(Event(Resource.Error(response.message!!, null)))
        }
    }

        fun observeNoteByID(noteID: String) = repository.observeNoteByID(noteID)

    }