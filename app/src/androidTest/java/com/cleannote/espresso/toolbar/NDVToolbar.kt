package com.cleannote.espresso.toolbar

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withParent
import com.cleannote.app.R
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

class NDVToolbar(matcher: Matcher<View>): NBaseView<NDVToolbar>(matcher) {
    val homeIcon = NImageView(ViewMatchers.withId(android.R.id.home))
    val title = NTextView(allOf(
        isAssignableFrom(TextView::class.java),
        withParent(isAssignableFrom(Toolbar::class.java))
    ))
}