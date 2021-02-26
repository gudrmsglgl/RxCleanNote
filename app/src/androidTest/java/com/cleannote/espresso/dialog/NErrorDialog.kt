package com.cleannote.espresso.dialog

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.espresso.button.NButton
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NErrorDialog(matcher: Matcher<View>): NBaseView<NErrorDialog>(matcher) {
    val title = NTextView(withId(R.id.md_text_title))
    val message = NTextView(withId(R.id.md_text_message))
    val positiveBtn = NButton(withId(R.id.md_button_positive))
}