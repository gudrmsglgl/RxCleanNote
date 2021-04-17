package com.cleannote.presentation

import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor

abstract class ArgumentCaptors<D> {

    private val onSuccessCaptor: KArgumentCaptor<OnSuccess<D>> = argumentCaptor()
    private val onErrorCaptor: KArgumentCaptor<OnError> = argumentCaptor()
    private val afterFinishedCaptor: KArgumentCaptor<Complete> = argumentCaptor()
    private val onCompleteCaptor: KArgumentCaptor<Complete> = argumentCaptor()

    fun onSuccessInvoke(data: D) {
        onSuccessCaptor.firstValue.invoke(data)
    }

    fun onErrorInvoke(data: Throwable) {
        onErrorCaptor.firstValue.invoke(data)
    }

    fun onAfterFinishValueCapturing() {
        afterFinishedCaptor.firstValue.invoke()
    }

    fun onCompleteInvoke() {
        onCompleteCaptor.firstValue.invoke()
    }

    fun onSuccessCapture() = onSuccessCaptor.capture()
    fun onErrorCapture() = onErrorCaptor.capture()
    fun onAfterFinishedCapture() = afterFinishedCaptor.capture()
    fun onCompleteCapture() = onCompleteCaptor.capture()
}
