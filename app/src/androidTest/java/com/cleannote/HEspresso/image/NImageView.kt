package com.cleannote.HEspresso.image

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.view.NBaseView
import org.hamcrest.Matcher
import org.hamcrest.Matchers

class NImageView(val matcher: Matcher<View>):
    NBaseView<NImageView>(matcher), ImageViewAssertions