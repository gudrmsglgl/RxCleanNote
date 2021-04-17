package com.cleannote.espresso.toolbar.detail

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.cleannote.app.R
import com.cleannote.espresso.view.NBaseView
import com.google.android.material.appbar.AppBarLayout
import org.hamcrest.Matcher

class NDAppbar(matcher: Matcher<View>) : NBaseView<NDAppbar>(matcher) {
    val toolbar = NDVToolbar(ViewMatchers.withId(R.id.tb_detail_view))

    fun collapse() {

        viewInteraction.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(AppBarLayout::class.java)
            }

            override fun getDescription(): String {
                return "Collapse App Bar Layout"
            }

            override fun perform(uiController: UiController, view: View?) {
                if (view is AppBarLayout) {
                    view.clearAnimation()
                    view.setExpanded(false, false)
                    uiController.loopMainThreadForAtLeast(100)
                }
            }
        })
    }

    fun swipeUp() {
        viewInteraction.perform(ViewActions.swipeUp())
    }
}
