package com.cleannote.presentation

import com.cleannote.domain.model.Query
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.mockito.ArgumentCaptor

open class ArgumentCaptors<R> {

    val onSuccessCaptor: KArgumentCaptor<OnSuccess<R>>  = argumentCaptor()
    val onErrorCaptor: KArgumentCaptor<OnError> = argumentCaptor()
    val afterFinishedCaptor: KArgumentCaptor<Complete> = argumentCaptor()
    val onCompleteCaptor: KArgumentCaptor<Complete> = argumentCaptor()


    fun onSuccessCapturing(data: R){
        onSuccessCaptor.firstValue.invoke(data)
    }

    fun onErrorCapturing(data: Throwable){
        onErrorCaptor.firstValue.invoke(data)
    }

    fun onAfterFinishCapturing(){
        afterFinishedCaptor.firstValue.invoke()
    }

    fun onCompleteCapturing(){
        onCompleteCaptor.firstValue.invoke()
    }


}