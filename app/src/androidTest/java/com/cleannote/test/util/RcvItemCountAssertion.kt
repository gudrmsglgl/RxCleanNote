package com.cleannote.test.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import junit.framework.Assert.assertTrue

class RcvItemCountAssertion(private val expectedCount: Int): ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null ) throw noViewFoundException
        val recyclerView = view as RecyclerView
        assertTrue(recyclerView.adapter!!.itemCount == expectedCount)
    }
}