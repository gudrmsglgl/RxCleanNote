package com.cleannote.presentation.extensions

import com.cleannote.domain.interactor.UseCase
import com.cleannote.presentation.Complete
import com.cleannote.presentation.OnError
import com.cleannote.presentation.OnSuccess
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.verify

fun <T, Param> UseCase<T, Param>.verifyExecute(
    onSuccessCaptor: KArgumentCaptor<OnSuccess<T>>,
    onErrorCaptor: KArgumentCaptor<OnError>,
    afterFinishedCaptor: KArgumentCaptor<Complete>,
    onCompleteCaptor: KArgumentCaptor<Complete>,
    paramCaptor: KArgumentCaptor<Param>
) = verify(this).execute(onSuccess = onSuccessCaptor.capture(),
    onError = onErrorCaptor.capture(),
    afterFinished = afterFinishedCaptor.capture(),
    onComplete = onCompleteCaptor.capture(),
    params = paramCaptor.capture()
)