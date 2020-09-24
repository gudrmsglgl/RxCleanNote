package com.cleannote.HEspresso.text

import androidx.annotation.StringRes
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.assertion.BaseAssertion
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not

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

    fun containText(text: String) {
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.withText(Matchers.containsString(text))
            )
        )
    }

    fun notContainText(text: String){
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.withText(not(text))
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