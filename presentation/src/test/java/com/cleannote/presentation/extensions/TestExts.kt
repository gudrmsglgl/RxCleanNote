package com.cleannote.presentation.extensions

import com.cleannote.domain.interactor.UseCase
import com.cleannote.presentation.ArgumentCaptors
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.verify

fun <T, Param> UseCase<T, Param>.verifyExecute(
    argumentCaptors: ArgumentCaptors<T>,
    paramCaptor: KArgumentCaptor<Param>
) = verify(this)
    .execute(
        onSuccess = argumentCaptors.onSuccessCapture(),
        onError = argumentCaptors.onErrorCapture(),
        afterFinished = argumentCaptors.onAfterFinishedCapture(),
        onComplete = argumentCaptors.onCompleteCapture(),
        params = paramCaptor.capture()
    )
