package com.cleannote.espresso.viewpager

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.viewpager2.widget.ViewPager2
import com.cleannote.espresso.actions.ScrollableActions
import org.hamcrest.Matcher

interface ViewPager2Actions: ScrollableActions {
    override fun scrollToStart() {
        viewInteraction.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isAssignableFrom(ViewPager2::class.java)

            override fun getDescription(): String = "Scroll view pager 2 to start"

            override fun perform(uiController: UiController, view: View?) {
                if (view is ViewPager2){
                    view.setCurrentItem(0, false)
                    uiController.loopMainThreadUntilIdle()
                }
            }
        })
    }

    override fun scrollToEnd() {
        viewInteraction.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isAssignableFrom(ViewPager2::class.java)

            override fun getDescription(): String = "Scroll view pager 2 to end"

            override fun perform(uiController: UiController, view: View?) {
                if (view is ViewPager2){
                    val endPosition = view.adapter?.itemCount?.let { it - 1 } ?: 0
                    view.setCurrentItem(endPosition, false)
                    uiController.loopMainThreadUntilIdle()
                }
            }
        })
    }

    override fun scrollTo(position: Int) {
        viewInteraction.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isAssignableFrom(ViewPager2::class.java)

            override fun getDescription(): String = "Scroll view pager 2 to specific position"

            override fun perform(uiController: UiController, view: View?) {
                if (view is ViewPager2){
                    view.setCurrentItem(position, false)
                    uiController.loopMainThreadUntilIdle()
                }
            }
        })
    }
}