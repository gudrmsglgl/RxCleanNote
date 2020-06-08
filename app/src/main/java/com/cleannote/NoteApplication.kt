package com.cleannote

import android.app.Application
import com.cleannote.injection.module.ApplicationComponent
import com.cleannote.injection.module.DaggerApplicationComponent

open class NoteApplication: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        initApplicationComponent()
    }

    open fun initApplicationComponent(){
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }

}