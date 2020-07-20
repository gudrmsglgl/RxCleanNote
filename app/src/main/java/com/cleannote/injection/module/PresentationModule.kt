package com.cleannote.injection.module

import androidx.lifecycle.ViewModelProvider
import com.cleannote.presentation.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class PresentationModule{

    @Binds
    abstract fun bindViewModelFactory(noteViewModelFactory: ViewModelFactory): ViewModelProvider.Factory



}