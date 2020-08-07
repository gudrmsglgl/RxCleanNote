package com.cleannote.HEspresso.text

import android.view.View
import com.cleannote.HEspresso.view.NBaseView
import org.hamcrest.Matcher

class NButton(private val matcher: Matcher<View>): NBaseView<NButton>(matcher), TextViewAssertions