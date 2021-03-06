package com.cleannote.espresso.search

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.app.R
import com.cleannote.espresso.button.NButton
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NSearchView(viewMatcher: Matcher<View>) : NBaseView<NSearchView>(viewMatcher) {
    val searchBtn =
        NButton(withId(androidx.appcompat.R.id.search_button))
    val searchEditView = NSearchEditorView(withId(R.id.search_src_text))
}
