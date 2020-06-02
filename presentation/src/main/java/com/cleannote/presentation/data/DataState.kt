package com.cleannote.presentation.data

open class DataState<out T> constructor(val status: State, val data: T?, val message: String?) {
    companion object{
        fun <T> success(data: T): DataState<T> = DataState(State.SUCCESS, data, null)
        fun <T> error(message: String?): DataState<T> = DataState(State.ERROR, null, message)
        fun <T> loading(): DataState<T> = DataState(State.LOADING, null, null)
    }
}

sealed class State{
    object SUCCESS: State()
    object ERROR: State()
    object LOADING: State()
}