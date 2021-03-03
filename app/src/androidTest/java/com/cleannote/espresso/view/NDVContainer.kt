package com.cleannote.espresso.view

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.app.R
import com.cleannote.espresso.actions.SwipeableActions
import com.cleannote.espresso.text.NTextView
import org.hamcrest.Matcher

class NDVContainer(matcher: Matcher<View>): NBaseView<NDVContainer>(matcher) {
    val content = NTextView(ViewMatchers.withId(R.id.tv_dv_content))
}