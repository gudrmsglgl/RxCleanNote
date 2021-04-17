package com.cleannote.espresso.image

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.app.R
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NEImageView(matcher: Matcher<View>) : NBaseView<NEImageView>(matcher), ImageViewAssertions {
    val icDelete = NImageView(withId(R.id.iv_ic_delete))
}
