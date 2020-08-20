package com.cleannote.HEspresso.scroll

import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.cleannote.HEspresso.actions.ScrollableActions
import org.hamcrest.Matcher
import org.hamcrest.Matchers

interface ScrollViewActions: ScrollableActions {

    override fun scrollToStart() {
        viewInteraction
            .perform(object: ViewAction{
                override fun getDescription(): String = "Scroll ScrollView to Start"

                override fun getConstraints(): Matcher<View> =
                    Matchers.allOf(ViewMatchers.isAssignableFrom(ScrollView::class.java), isDisplayed())

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        view.fullScroll(View.FOCUS_UP)
                    }
                }
            })
    }

    override fun scrollToEnd() {
        viewInteraction
            .perform(object: ViewAction {
                override fun getDescription(): String = "Scroll scrollView to End"

                override fun getConstraints(): Matcher<View> =
                    Matchers.allOf(ViewMatchers.isAssignableFrom(ScrollView::class.java), isDisplayed())

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        view.fullScroll(View.FOCUS_DOWN)
                    }
                }
            })
    }

    override fun scrollTo(position: Int) {
        viewInteraction
            .perform(object: ViewAction {
                override fun getDescription(): String = "Scroll scrollView to $position"

                override fun getConstraints(): Matcher<View> =
                    Matchers.allOf(ViewMatchers.isAssignableFrom(ScrollView::class.java), isDisplayed())

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        view.scrollTo(0, position)
                    }
                }
            })
    }

}