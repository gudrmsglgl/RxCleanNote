package com.cleannote.espresso.viewpager

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NViewPager(val matcher: Matcher<View>) :
    NBaseView<NViewPager>(matcher),
    ViewPager2Actions {

    inline fun firstItem(function: NViewPagerItem.() -> Unit) {
        scrollToStart()
        childAt(0, function)
    }

    inline fun childAt(position: Int, function: NViewPagerItem.() -> Unit) {
        scrollTo(position)
        function(NViewPagerItem(matcher))
    }
}
