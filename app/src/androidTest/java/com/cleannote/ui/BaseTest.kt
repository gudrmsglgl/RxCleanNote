package com.cleannote.ui

import androidx.test.core.app.ApplicationProvider
import com.cleannote.TestBaseApplication
import com.cleannote.injection.TestApplicationComponent

abstract class BaseTest {

    val application: TestBaseApplication
        = ApplicationProvider.getApplicationContext() as TestBaseApplication

    fun getComponent(): TestApplicationComponent {
        return application.applicationComponent as TestApplicationComponent
    }

    abstract fun injectTest()
}