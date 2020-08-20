package com.cleannote.HEspresso.scroll

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.edit.NDEditText
import com.cleannote.HEspresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NDScrollView(matcher: Matcher<View>): NBaseView<NDScrollView>(matcher) {
    val body = NDEditText(ViewMatchers.withId(R.id.note_body))
}