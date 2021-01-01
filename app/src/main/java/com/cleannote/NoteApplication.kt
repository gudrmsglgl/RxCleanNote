package com.cleannote

import android.app.Application
import com.cleannote.app.BuildConfig
import com.cleannote.injection.module.ApplicationComponent
import com.cleannote.injection.module.DaggerApplicationComponent
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import timber.log.Timber
import timber.log.Timber.DebugTree


open class NoteApplication: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            if (FlipperUtils.shouldEnableFlipper(this)){
                val client: FlipperClient = AndroidFlipperClient.getInstance(this)
                client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
                client.start()
            }
        }
        initApplicationComponent()
    }

    open fun initApplicationComponent(){
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }

}