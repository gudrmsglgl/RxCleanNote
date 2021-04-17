package com.cleannote.espresso.button

import android.view.View
import com.cleannote.espresso.text.TextViewAssertions
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NButton(private val matcher: Matcher<View>) :
    NBaseView<NButton>(matcher),
    TextViewAssertions
