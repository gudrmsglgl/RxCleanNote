package com.cleannote.domain.interactor

interface UseCase<T, in Params> {
    fun execute(observer: T, params: Params?)
}