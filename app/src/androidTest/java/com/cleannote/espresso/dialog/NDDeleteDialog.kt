package com.cleannote.espresso.dialog

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.app.R
import com.cleannote.espresso.button.NButton
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NDDeleteDialog(matcher: Matcher<View>) : NBaseView<NDDeleteDialog>(matcher) {
    val title = NTextView(withId(R.id.md_text_title))
    val positiveBtn = NButton(withText(R.string.dialog_ok))
    val negativeBtn = NButton(withId(R.id.md_button_negative))
}
