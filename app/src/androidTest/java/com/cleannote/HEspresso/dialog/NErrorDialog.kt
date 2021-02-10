package com.cleannote.HEspresso.dialog

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.HEspresso.button.NButton
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.HEspresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NErrorDialog(matcher: Matcher<View>): NBaseView<NErrorDialog>(matcher) {
    val title = NTextView(withId(R.id.md_text_title))
    val message = NTextView(withText(R.string.searchErrorMsg))
    val positiveBtn = NButton(withId(R.id.md_button_positive))
}