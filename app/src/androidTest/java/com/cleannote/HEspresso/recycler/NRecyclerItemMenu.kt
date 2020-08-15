package com.cleannote.HEspresso.recycler

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.image.NImageView
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.HEspresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NRecyclerItemMenu(matcher: Matcher<View>): NBaseView<NRecyclerItemMenu>(matcher) {
    val deleteImg = NImageView(withId(R.id.swipe_delete_img))
    val deleteText = NTextView(withId(R.id.swipe_delete_text))
}