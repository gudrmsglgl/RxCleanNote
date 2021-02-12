package com.cleannote.espresso.check

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.espresso.assertion.BaseAssertion

interface CheckableAssertions: BaseAssertion {

    fun isChecked() {
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.isChecked()
            )
        )
    }

    fun isNotChecked(){
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.isNotChecked()
            )
        )
    }
}