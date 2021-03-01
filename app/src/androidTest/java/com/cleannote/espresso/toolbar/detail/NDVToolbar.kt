package com.cleannote.espresso.toolbar.detail

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.cleannote.app.R
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.toolbar.NToolbarTitle
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf

class NDVToolbar(matcher: Matcher<View>): NBaseView<NDVToolbar>(matcher) {
    val homeIcon = NImageView(ViewMatchers.withId(android.R.id.home))
    val title = NToolbarTitle(matcher)
    val editIcon = NImageView(ViewMatchers.withId(R.id.menu_edit))
}