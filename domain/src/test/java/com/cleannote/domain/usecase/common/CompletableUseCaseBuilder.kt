package com.cleannote.domain.usecase.common

import io.reactivex.Completable

interface CompletableUseCaseBuilder<Param>: UseCaseBuilder<Completable, Param>