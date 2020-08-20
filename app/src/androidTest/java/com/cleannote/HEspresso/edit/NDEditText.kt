package com.cleannote.HEspresso.edit

import android.view.View
import com.cleannote.HEspresso.actions.SwipeableActions
import com.cleannote.HEspresso.view.NBaseView
import org.hamcrest.Matcher

class NDEditText(matcher: Matcher<View>): NBaseView<NDEditText>(matcher),
    EditableActions, EditAssertions, SwipeableActions