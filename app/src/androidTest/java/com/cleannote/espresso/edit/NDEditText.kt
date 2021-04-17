package com.cleannote.espresso.edit

import android.view.View
import com.cleannote.espresso.actions.SwipeableActions
import com.cleannote.espresso.assertion.CollapseAssertion
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NDEditText(matcher: Matcher<View>) :
    NBaseView<NDEditText>(matcher),
    EditableActions,
    EditAssertions,
    SwipeableActions,
    CollapseAssertion
