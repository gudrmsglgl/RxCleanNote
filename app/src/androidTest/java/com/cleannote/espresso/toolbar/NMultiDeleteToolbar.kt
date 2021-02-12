package com.cleannote.espresso.toolbar

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NMultiDeleteToolbar(matcher: Matcher<View>): NBaseView<NMultiDeleteToolbar>(matcher) {
    val title = NTextView(withId(R.id.tv_multi_delete_title))
    val btnCancel = NImageView(withId(R.id.btn_multi_delete_cancel))
    val btnConfirm = NImageView(withId(R.id.btn_multi_delete_ok))
}