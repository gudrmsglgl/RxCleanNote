package com.cleannote

import android.app.Application
import com.cleannote.app.BuildConfig
import com.cleannote.injection.module.ApplicationComponent
import com.cleannote.injection.module.DaggerApplicationComponent
import timber.log.Timber
import timber.log.Timber.DebugTree

open class NoteApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        initApplicationComponent()
    }

    open fun initApplicationComponent() {
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}
