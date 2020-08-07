package com.cleannote.HEspresso.text

import androidx.annotation.StringRes
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.assertion.BaseAssertion

interface TextViewAssertions: BaseAssertion {

    fun hasEmptyText(){
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.withText("")
            )
        )
    }

    fun hasText(text: String){
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.withText(text)
            )
        )
    }

    fun hasText(@StringRes resId: Int) {
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.withText(resId)
            )
        )
    }

    fun isChecked(){
        viewInteraction.check(
            ViewAssertions.matches(ViewMatchers.isChecked())
        )
    }

    fun isNotChecked(){
        viewInteraction.check(
            ViewAssertions.matches(ViewMatchers.isNotChecked())
        )
    }
}