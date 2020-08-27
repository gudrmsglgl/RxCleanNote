package com.cleannote.domain.usecase.common

interface UseCaseBuilder<T, Param> {
    fun whenBuildUseCase(param: Param): T
}