package com.cleannote.domain.interactor

abstract class UseCaseManager{

    private val _useCases: MutableList<UseCase<*,*>> = mutableListOf()

    fun addUseCases(vararg useCases: UseCase<*, *>){
        _useCases.addAll(useCases)
    }

    fun disposeUseCases(){
        _useCases.forEach { it.dispose() }
    }

}