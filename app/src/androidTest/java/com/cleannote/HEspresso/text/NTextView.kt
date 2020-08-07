package com.cleannote.HEspresso.text

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.assertion.BaseAssertion
import com.cleannote.HEspresso.view.NBaseView
import org.hamcrest.Matcher

class NTextView(private val viewMatchers: Matcher<View>):
    NBaseView<NTextView>(viewMatchers), TextViewAssertions