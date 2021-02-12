package com.cleannote.espresso.edit

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NEditText(matcher: Matcher<View>): NBaseView<NEditText>(matcher), EditableActions, EditAssertions