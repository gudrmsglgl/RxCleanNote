package com.cleannote.HEspresso.view

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import com.cleannote.HEspresso.actions.BaseActions
import com.cleannote.HEspresso.assertion.BaseAssertion
import org.hamcrest.Matcher

@Suppress("UNCHECKED_CAST")
open class NBaseView<out T>(private val viewMatcher: Matcher<View>): BaseActions, BaseAssertion {

    override val viewInteraction: ViewInteraction
        get() = onView(viewMatcher)

    operator fun invoke(function: T.() -> Unit){
        function(this as T)
    }

    fun perform(function: T.() -> Unit): T {
        function(this as T)
        return this
    }

}