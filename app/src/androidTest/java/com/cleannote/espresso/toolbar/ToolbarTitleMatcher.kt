package com.cleannote.espresso.toolbar

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description

class ToolbarTitleMatcher(private val title: String?) : BoundedMatcher<View, Toolbar>(Toolbar::class.java) {

    private var actualTitle: String? = null

    override fun describeTo(description: Description) {
        description.appendText("Title Expect $title")
        description.appendText("But Actual is not equals to $actualTitle")
    }

    override fun matchesSafely(view: Toolbar?): Boolean {
        actualTitle = view?.title?.toString()
        return view?.title?.toString() == title
    }
}
