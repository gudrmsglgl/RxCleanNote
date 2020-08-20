package com.cleannote.ui.screen

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers

@Suppress("UNCHECKED_CAST")
abstract class BaseScreen<out T> {

    operator fun invoke(function: T.() -> Unit){
        function(this as T)
    }

    fun idle(duration: Long = 1000L) {
        Espresso.onView(ViewMatchers.isRoot()).perform(object : ViewAction {
            override fun getDescription() = "Idle for $duration milliseconds"

            override fun getConstraints() = ViewMatchers.isAssignableFrom(View::class.java)

            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(duration)
            }
        })
    }

}