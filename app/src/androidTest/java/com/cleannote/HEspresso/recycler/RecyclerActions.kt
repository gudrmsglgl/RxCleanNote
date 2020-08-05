package com.cleannote.HEspresso.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.actions.BaseActions
import org.hamcrest.Matcher
import org.hamcrest.Matchers

interface RecyclerActions: BaseActions {

    fun getSize(): Int {
        var size = 0

        viewInteraction.perform(object : ViewAction{
            override fun getDescription(): String = "Get RecyclerView adapter size"

            override fun getConstraints(): Matcher<View> =
                Matchers.allOf(ViewMatchers.isAssignableFrom(RecyclerView::class.java), ViewMatchers.isDisplayed())

            override fun perform(uiController: UiController?, view: View?) {
                if (view is RecyclerView)
                    size = view.adapter?.itemCount!!
            }
        })
        return size
    }
}