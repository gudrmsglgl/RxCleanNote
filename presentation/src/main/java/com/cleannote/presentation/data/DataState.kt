package com.cleannote.presentation.data

open class DataState<out T> constructor(val status: State, val data: T?, val throwable: Throwable?) {
    val isLoading
        get() = this.status == State.LOADING

    companion object{
        fun <T> success(data: T?): DataState<T> = DataState(State.SUCCESS, data, null)
        fun <T> error(throwable: Throwable?, data: T? = null): DataState<T> = DataState(State.ERROR, data, throwable)
        fun <T> loading(): DataState<T> = DataState(State.LOADING, null, null)
    }
}

sealed class State{
    object SUCCESS: State()
    object ERROR: State()
    object LOADING: State()
}