package com.cleannote

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

class MyTestRunner: AndroidJUnitRunner() {

    /*override fun onStart() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        super.onStart()
    }*/

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestBaseApplication::class.java.name, context)
    }
}