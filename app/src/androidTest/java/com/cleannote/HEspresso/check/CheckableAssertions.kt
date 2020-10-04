package com.cleannote.HEspresso.check

import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.assertion.BaseAssertion

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