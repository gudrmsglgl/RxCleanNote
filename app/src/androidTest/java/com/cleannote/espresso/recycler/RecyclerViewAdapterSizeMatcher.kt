package com.cleannote.espresso.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

class RecyclerViewAdapterSizeMatcher(private val expectedSize: Int): BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
    private var itemCount: Int = 0

    override fun describeTo(description: Description) {
        description
            .appendText("RecyclerView with ")
            .appendValue(expectedSize)
            .appendText(" item(s), but got with ")
            .appendValue(itemCount)
    }

    override fun matchesSafely(view: RecyclerView): Boolean = run {
        itemCount = view.adapter?.itemCount ?: 0
        itemCount == expectedSize
    }

}