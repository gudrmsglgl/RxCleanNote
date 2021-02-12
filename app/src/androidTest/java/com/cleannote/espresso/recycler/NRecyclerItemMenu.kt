package com.cleannote.espresso.recycler

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NRecyclerItemMenu(matcher: Matcher<View>): NBaseView<NRecyclerItemMenu>(matcher) {
    val deleteImg = NImageView(withId(R.id.swipe_delete_img))
}