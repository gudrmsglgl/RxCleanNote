package com.cleannote.HEspresso.toolbar

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.image.NImageView
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.HEspresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NDToolbar(matcher: Matcher<View>): NBaseView<NDToolbar>(matcher) {
    val primaryMenu = NImageView(ViewMatchers.withId(R.id.toolbar_primary_icon))
    val toolbarTitle = NTextView(ViewMatchers.withId(R.id.tool_bar_title))
    val secondMenu = NImageView(ViewMatchers.withId(R.id.toolbar_secondary_icon))
}