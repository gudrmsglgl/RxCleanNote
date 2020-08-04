package com.cleannote.HEspresso.text

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.assertion.BaseAssertion

class NTextView(@IdRes val idRes: Int):
    TextViewAssertions {

    override val viewInteraction: ViewInteraction
        get() = onView(ViewMatchers.withId(idRes))

    operator fun invoke(function: NTextView.()->Unit){
        function(this)
    }
}