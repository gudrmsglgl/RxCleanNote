package com.cleannote.injection.module

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    CacheModule::class
))
interface ApplicationComponent {

    @Component.Builder
    interface Builder{
        fun build(@BindsInstance application: Application): ApplicationComponent
    }
}