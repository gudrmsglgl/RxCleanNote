package com.cleannote.HEspresso.recycler

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.actions.BaseActions
import com.cleannote.HEspresso.actions.ScrollableActions
import com.cleannote.HEspresso.actions.SwipeableActions
import org.hamcrest.Matcher
import org.hamcrest.Matchers

interface RecyclerActions: ScrollableActions, SwipeableActions {

    override fun scrollToStart() {
        viewInteraction.perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(0))
    }

    override fun scrollToEnd() {
        viewInteraction.perform(object : ViewAction{
            override fun getDescription(): String = "Scroll RecyclerView to the bottom"


            override fun getConstraints(): Matcher<View> = ViewMatchers.isAssignableFrom(RecyclerView::class.java)

            override fun perform(uiController: UiController, view: View?) {
                if (view is RecyclerView) {
                    val position = view.adapter!!.itemCount-1
                    view.scrollToPosition(position)
                    uiController.loopMainThreadUntilIdle()
                    val lastView = view.findViewHolderForAdapterPosition(position)!!.itemView
                    view.scrollBy(0, lastView.height)
                    uiController.loopMainThreadUntilIdle()
                }
            }
        })
    }

    override fun scrollTo(position: Int) {
        viewInteraction.perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
    }

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

    fun getLastVisiblePosition(): Int {
        var lastVisiblePosition = 0

        viewInteraction.perform(object : ViewAction{
            override fun getDescription(): String = "Get RecyclerView Item LastVisible size"

            override fun getConstraints(): Matcher<View> =
                Matchers.allOf(ViewMatchers.isAssignableFrom(RecyclerView::class.java), ViewMatchers.isDisplayed())

            override fun perform(uiController: UiController?, view: View?) {
                if (view is RecyclerView)
                    lastVisiblePosition = (view.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            }
        })
        return lastVisiblePosition
    }
}