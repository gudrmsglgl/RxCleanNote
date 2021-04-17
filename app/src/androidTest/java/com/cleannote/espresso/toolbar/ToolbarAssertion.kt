package com.cleannote.espresso.toolbar

import androidx.test.espresso.assertion.ViewAssertions
import com.cleannote.espresso.text.TextViewAssertions
import com.cleannote.espresso.util.getResourceString

interface ToolbarAssertion : TextViewAssertions {
    override fun hasEmptyText() {
        viewInteraction.check(ViewAssertions.matches(ToolbarTitleMatcher("")))
    }

    override fun hasText(resId: Int) {
        viewInteraction.check(ViewAssertions.matches(ToolbarTitleMatcher(getResourceString(resId))))
    }

    override fun hasText(text: String) {
        viewInteraction.check(ViewAssertions.matches(ToolbarTitleMatcher(text)))
    }
}
