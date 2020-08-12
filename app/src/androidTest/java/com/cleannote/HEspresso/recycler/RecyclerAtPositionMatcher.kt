package com.cleannote.HEspresso.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class RecyclerAtPositionMatcher(private val position: Int, private val matcher: Matcher<View>):
    BoundedMatcher<View, RecyclerView>(RecyclerView::class.java)
{
    override fun describeTo(description: Description) {
        description.appendText("has item at position " + position + ": ");
        matcher.describeTo(description);
    }

    override fun matchesSafely(view: RecyclerView): Boolean {
        val viewHolder = view.findViewHolderForAdapterPosition(position)
        return if (viewHolder == null ) false
        else matcher.matches(viewHolder.itemView)
    }
}