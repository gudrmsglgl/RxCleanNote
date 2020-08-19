package com.cleannote.HEspresso.edit

import android.view.View
import com.cleannote.HEspresso.view.NBaseView
import org.hamcrest.Matcher

class NEditText(matcher: Matcher<View>): NBaseView<NEditText>(matcher), EditableActions, EditAssertions