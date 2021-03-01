package com.cleannote.espresso.toolbar.list

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.search.NSearchView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.app.R
import com.cleannote.espresso.toolbar.ToolbarAssertion
import org.hamcrest.Matcher

class NSearchToolbar(private val viewMatcher: Matcher<View>):
    NBaseView<NSearchToolbar>(viewMatcher), ToolbarAssertion
{
    val filterMenu = NImageView(ViewMatchers.withId(R.id.action_filter))
    val searchView = NSearchView(ViewMatchers.withId(R.id.sv))
}