package com.cleannote.HEspresso.search

import android.view.View
import com.cleannote.HEspresso.view.NBaseView
import org.hamcrest.Matcher

class NSearchEditorView(viewMatcher: Matcher<View>):
    NBaseView<NSearchEditorView>(viewMatcher), NSearchActions
