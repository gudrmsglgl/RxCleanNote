package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.app.R
import com.cleannote.espresso.toolbar.NDVToolbar

object DetailViewScreen: BaseScreen<DetailViewScreen>() {
    val toolbar = NDVToolbar(ViewMatchers.withId(R.id.tb_detail_view))
}