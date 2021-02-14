package com.cleannote.espresso.scroll

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.espresso.edit.NDEditText
import com.cleannote.espresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NDScrollView(matcher: Matcher<View>): NBaseView<NDScrollView>(matcher) {
    val body = NDEditText(ViewMatchers.withId(R.id.note_body))
}