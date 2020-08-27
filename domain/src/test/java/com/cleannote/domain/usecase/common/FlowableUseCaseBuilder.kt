package com.cleannote.domain.usecase.common

import io.reactivex.Flowable

interface FlowableUseCaseBuilder<T,Param>: UseCaseBuilder<Flowable<T>, Param>