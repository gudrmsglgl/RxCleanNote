package com.cleannote.espresso.viewpager

import android.view.View
import com.cleannote.espresso.actions.ScrollableActions
import com.cleannote.espresso.scroll.ScrollViewActions
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NViewPager(matcher: Matcher<View>): NBaseView<NViewPager>(matcher), ScrollViewActions {
    inline fun <reified T: NViewPagerItem> childAt(position: Int, function: T.() -> Unit) {

    }
}