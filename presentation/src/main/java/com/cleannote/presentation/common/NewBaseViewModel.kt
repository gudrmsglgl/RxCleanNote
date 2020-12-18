package com.cleannote.presentation.common

import androidx.lifecycle.ViewModel
import com.cleannote.domain.interactor.UseCaseManager

open class NewBaseViewModel(private val useCaseManager: UseCaseManager): ViewModel() {
    override fun onCleared() {
        useCaseManager.disposeUseCases()
    }
}