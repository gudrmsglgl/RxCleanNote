package com.cleannote

import android.app.Application
import com.cleannote.app.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class NoteApplication: Application() {

    //lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        //initApplicationComponent()
    }

    /*open fun initApplicationComponent(){
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }*/

}