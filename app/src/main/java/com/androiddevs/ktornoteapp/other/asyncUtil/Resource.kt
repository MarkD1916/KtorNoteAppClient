package com.androiddevs.ktornoteapp.other.asyncUtil

sealed class Resource<T>(val data: T? = null, val message: String? = null, val info:HashMap<String, T>? =null ) {

    class Success<T>(data: T): Resource<T>(data)

    class Error<T> (message: String, data: T?=null): Resource<T>(data, message)

    class Loading<T>(data: T?=null): Resource<T>(data)
}