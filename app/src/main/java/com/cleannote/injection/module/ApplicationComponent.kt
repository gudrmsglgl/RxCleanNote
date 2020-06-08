package com.cleannote.injection.module

import android.app.Application
import com.cleannote.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    CacheModule::class,
    DataModule::class,
    DomainModule::class,
    PresentationModule::class,
    RemoteModule::class,
    UiModule::class
))
interface ApplicationComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance application: Application): ApplicationComponent
    }

    fun inject(mainActivity: MainActivity)
}