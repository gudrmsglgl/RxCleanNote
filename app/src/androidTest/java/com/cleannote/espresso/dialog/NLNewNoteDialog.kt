package com.cleannote.espresso.dialog

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.espresso.edit.NEditText
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NLNewNoteDialog(matcher: Matcher<View>): NBaseView<NLNewNoteDialog>(matcher) {
    val message = NTextView(withText(R.string.dialog_new_note))
    val inputTitle = NEditText(withId(R.id.md_input_message))
}