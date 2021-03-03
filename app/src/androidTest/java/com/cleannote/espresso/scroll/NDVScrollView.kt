package com.cleannote.espresso.scroll

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.app.R
import com.cleannote.espresso.actions.SwipeableActions
import com.cleannote.espresso.assertion.CollapseAssertion
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.espresso.view.NDVContainer
import org.hamcrest.Matcher

class NDVScrollView(matcher: Matcher<View>): NBaseView<NDVScrollView>(matcher)
{
    val title = NTextView(ViewMatchers.withId(R.id.tv_dv_title))
    val updateTime = NTextView(ViewMatchers.withId(R.id.tv_dv_update_time))
    val contentContainer = NDVContainer(ViewMatchers.withId(R.id.dv_content_container))
}