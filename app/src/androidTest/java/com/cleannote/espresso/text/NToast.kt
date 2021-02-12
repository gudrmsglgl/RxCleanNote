package com.cleannote.espresso.text

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import com.cleannote.espresso.actions.BaseActions
import com.cleannote.espresso.assertion.BaseAssertion
import org.hamcrest.Matcher

class NToast(val matcher: Matcher<View>): BaseActions, BaseAssertion {
    override val viewInteraction: ViewInteraction
        get() = onView(matcher).inRoot(ToastMatcher())

    operator fun invoke(function: NToast.() -> Unit){
        function(this)
    }
}