package com.cleannote.domain.interactor

abstract class UseCaseManager(vararg param: UseCase<*,*>){

    private val _useCases: HashSet<UseCase<*,*>> = hashSetOf()

    init {
        addUseCases(*param)
    }

    private fun addUseCases(vararg useCases: UseCase<*, *>){
        _useCases.addAll(useCases)
    }

    fun disposeUseCases(){
        _useCases.forEach { it.dispose() }
    }

}