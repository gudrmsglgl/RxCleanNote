package com.cleannote.presentation.common

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cleannote.domain.interactor.UseCase

abstract class BaseViewModel(vararg useCases: UseCase<*,*>): ViewModel() {
    private var useCaseList: MutableList<UseCase<*,*>> = mutableListOf()

    init {
        useCaseList.addAll(useCases)
    }

    override fun onCleared() {
        super.onCleared()
        useCaseList.forEach { it.dispose() }
    }

}