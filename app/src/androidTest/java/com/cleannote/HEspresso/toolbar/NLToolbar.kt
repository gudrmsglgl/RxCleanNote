package com.cleannote.HEspresso.toolbar

import android.content.res.Resources
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.dialog.NLFilterDialog
import com.cleannote.HEspresso.image.NImageView
import com.cleannote.HEspresso.search.NSearchView
import com.cleannote.HEspresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NLToolbar(private val viewMatcher: Matcher<View>):
    NBaseView<NLToolbar>(viewMatcher),ToolbarAssertion
{
    val filterMenu = NImageView(ViewMatchers.withId(R.id.action_filter))
    val filterDialog = NLFilterDialog(ViewMatchers.withId(R.id.filter_dialog))
    val searchView = NSearchView(ViewMatchers.withId(R.id.search_view))
}