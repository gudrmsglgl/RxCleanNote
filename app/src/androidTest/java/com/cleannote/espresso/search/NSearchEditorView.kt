package com.cleannote.espresso.search

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NSearchEditorView(viewMatcher: Matcher<View>):
    NBaseView<NSearchEditorView>(viewMatcher), NSearchActions
