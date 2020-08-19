package com.cleannote.HEspresso.dialog

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.HEspresso.edit.NEditText
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.HEspresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NLNewNoteDialog(matcher: Matcher<View>): NBaseView<NLNewNoteDialog>(matcher) {
    val message = NTextView(withText(R.string.dialog_newnote))
    val editTitle = NEditText(withId(R.id.md_input_message))
}