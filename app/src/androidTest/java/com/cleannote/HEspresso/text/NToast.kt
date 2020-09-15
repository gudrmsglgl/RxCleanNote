package com.cleannote.HEspresso.text

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import com.cleannote.HEspresso.actions.BaseActions
import com.cleannote.HEspresso.assertion.BaseAssertion
import com.cleannote.HEspresso.view.NBaseView
import org.hamcrest.Matcher

class NToast(val matcher: Matcher<View>): BaseActions, BaseAssertion {
    override val viewInteraction: ViewInteraction
        get() = onView(matcher).inRoot(ToastMatcher())

    operator fun invoke(function: NToast.() -> Unit){
        function(this)
    }
}