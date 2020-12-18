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


    fun fetchSuccessData(data: R){
        onSuccessCaptor.firstValue.invoke(data)
    }

    fun fetchErrorData(data: Throwable){
        onErrorCaptor.firstValue.invoke(data)
    }

    fun fetchAfterFinished(){
        afterFinishedCaptor.firstValue.invoke()
    }

    fun fetchComplete(){
        onCompleteCaptor.firstValue.invoke()
    }


}