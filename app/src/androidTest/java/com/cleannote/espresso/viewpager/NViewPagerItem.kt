package com.cleannote.espresso.viewpager

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.app.R
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NViewPagerItem(matcher: Matcher<View>): NBaseView<NViewPagerItem>(matcher) {
    val image = NImageView(ViewMatchers.withId(R.id.detail_vp_item))
}