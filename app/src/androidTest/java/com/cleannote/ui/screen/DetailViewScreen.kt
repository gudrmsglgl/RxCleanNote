package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.app.R
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.scroll.NDVScrollView
import com.cleannote.espresso.toolbar.detail.NDVToolbar
import com.cleannote.espresso.viewpager.NViewPager

object DetailViewScreen: BaseScreen<DetailViewScreen>() {
    val toolbar = NDVToolbar(ViewMatchers.withId(R.id.tb_detail_view))
    val headerViewPager = NViewPager(ViewMatchers.withId(R.id.image_pager))
    val emptyImage = NImageView(ViewMatchers.withId(R.id.iv_empty))
    val body = NDVScrollView(ViewMatchers.withId(R.id.bottomSheet))
}