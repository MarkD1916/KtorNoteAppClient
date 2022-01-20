package com.androiddevs.ktornoteapp.ui.note

import androidx.lifecycle.*
import com.androiddevs.ktornoteapp.data.local.model.Note
import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import com.androiddevs.ktornoteapp.other.Event
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import com.androiddevs.ktornoteapp.repository.main.MainRepository
import com.androiddevs.ktornoteapp.repository.main.MainRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: MainRepositoryImpl
) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _allNotes = _forceUpdate.switchMap {
        repository.getAllNotes().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }
    val allNotes: LiveData<Event<Resource<List<Note>>>> = _allNotes

    fun syncAllNotes() = _forceUpdate.postValue(true)
}