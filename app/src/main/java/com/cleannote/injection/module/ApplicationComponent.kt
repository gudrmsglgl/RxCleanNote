package com.cleannote.injection.module

import com.cleannote.MainActivity
import com.cleannote.NoteApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    CacheModule::class,
    DataModule::class,
    DomainModule::class,
    PresentationModule::class,
    RemoteModule::class,
    UiModule::class,
    ApplicationModule::class
])
interface ApplicationComponent {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance application: NoteApplication): ApplicationComponent
    }

    fun inject(mainActivity: MainActivity)
}