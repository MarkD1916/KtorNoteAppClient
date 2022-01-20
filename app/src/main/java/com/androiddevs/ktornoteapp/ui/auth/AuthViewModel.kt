package com.androiddevs.ktornoteapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.ktornoteapp.data.remote.responses.SimpleResponse
import com.androiddevs.ktornoteapp.other.Event
import com.androiddevs.ktornoteapp.other.asyncUtil.Resource
import com.androiddevs.ktornoteapp.repository.auth.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepositoryImpl: AuthRepositoryImpl
    ) : ViewModel() {

    private val _registerStatus = MutableLiveData<Event<Resource<SimpleResponse>>>()
    val registerStatus: LiveData<Event<Resource<SimpleResponse>>> = _registerStatus

    private val _loginStatus = MutableLiveData<Event<Resource<SimpleResponse>>>()
    val loginStatus: LiveData<Event<Resource<SimpleResponse>>> = _loginStatus

    fun register(email:String, password:String, confirmPassword:String){
        _registerStatus.postValue(Event(Resource.Loading()))

        if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            _registerStatus.postValue(Event(Resource.Error("Please fill out all fields")))
            return
        }

        if(password!=confirmPassword){
            _registerStatus.postValue(Event(Resource.Error("The password do not match")))
        }
        viewModelScope.launch(Dispatchers.IO){
            val response = authRepositoryImpl.register(email,password)
            response.data?.body()?.let {
                _registerStatus.postValue(Event(Resource.Success(it)))
                return@launch
            }
            _registerStatus.postValue(Event(Resource.Error(response.message!!, null)))
        }
    }

    fun login(email:String, password:String){
        _loginStatus.postValue(Event(Resource.Loading(null)))

        if(email.isEmpty() || password.isEmpty()){
            _loginStatus.postValue(Event(Resource.Error("Please fill out all fields")))
            return
        }

        viewModelScope.launch(Dispatchers.IO){
            val response = authRepositoryImpl.login(email,password)
            response.data?.body()?.let {
                _loginStatus.postValue(Event(Resource.Success(it)))
                return@launch
            }
            _loginStatus.postValue(Event(Resource.Error(response.message!!)))
        }
    }

}