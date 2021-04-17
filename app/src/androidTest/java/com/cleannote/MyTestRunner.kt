package com.cleannote

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class MyTestRunner : AndroidJUnitRunner() {

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
