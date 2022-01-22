package com.androiddevs.ktornoteapp.other.asyncUtil

import androidx.lifecycle.Observer

class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (!hasBeenHandled) {
            hasBeenHandled = true
            content
        } else null
    }

    fun peekContent() = content
}

class EventObserver<T>(
    private inline val onError: ((Pair<String,T?>) -> Unit)? = null,
    private inline val onLoading: ((T?) -> Unit)? = null,
    private inline val onSuccess: (T) -> Unit
) : Observer<Event<Resource<T>>> {
    override fun onChanged(t: Event<Resource<T>>?) {
        when(val content =t?.peekContent()){
            is Resource.Success -> {
                content.data?.let(onSuccess)
            }
            is Resource.Error ->{
                t.getContentIfNotHandled()?.let {
                    onError?.let {error->
                        content.data?.let{data->
                            error(Pair(it.message!!,data))
                        }
                        error(Pair(it.message!!,null))
                    }
                }

            }

            is Resource.Loading ->{
                onLoading?.let{loading->
                    loading(content.data)
                }
            }
        }
    }

}