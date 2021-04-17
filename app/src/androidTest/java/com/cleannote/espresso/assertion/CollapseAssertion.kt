package com.cleannote.espresso.assertion

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.app.R
import com.cleannote.espresso.util.getResourceString

interface CollapseAssertion : BaseAssertion {
    fun stateExpanded() = viewInteraction.check(
        ViewAssertions.matches(
            ViewMatchers.withContentDescription(getResourceString(R.string.desc_state_expanded))
        )
    )
    fun stateCollapse() = viewInteraction.check(
        ViewAssertions.matches(
            ViewMatchers.withContentDescription(getResourceString(R.string.desc_state_collapse))
        )
    )
}
