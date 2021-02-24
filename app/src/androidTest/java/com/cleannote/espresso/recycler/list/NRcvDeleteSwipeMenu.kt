package com.cleannote.espresso.recycler.list

import android.view.View
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.view.NBaseView
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