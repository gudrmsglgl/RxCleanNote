package com.cleannote.espresso.text

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NTextView(private val viewMatchers: Matcher<View>):
    NBaseView<NTextView>(viewMatchers), TextViewAssertions