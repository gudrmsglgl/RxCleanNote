package com.cleannote.espresso.image

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NImageView(val matcher: Matcher<View>):
    NBaseView<NImageView>(matcher), ImageViewAssertions