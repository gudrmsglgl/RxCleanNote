package com.cleannote.espresso.toolbar

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NToolbarTitle(matcher: Matcher<View>): NBaseView<NToolbarTitle>(matcher), ToolbarAssertion