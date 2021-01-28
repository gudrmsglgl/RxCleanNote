package com.cleannote.HEspresso.recycler

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.image.NImageView
import com.cleannote.HEspresso.view.NBaseView
import com.cleannote.app.R
import com.cleannote.test.util.RecyclerViewMatcher
import org.hamcrest.Matcher

class NRcvDeleteSwipeMenu(
    idRes: Int,
    position:Int,
    matcher: Matcher<View>
): NBaseView<NRcvDeleteSwipeMenu>(matcher) {

    val deleteImg = NImageView(RecyclerViewMatcher(idRes).atPositionOnView(position, R.id.swipe_delete_img))
}