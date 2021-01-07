package com.cleannote.presentation

typealias OnSuccess<T> = (T) -> Unit
typealias OnError = (Throwable) -> Unit
typealias Complete = () -> Unit