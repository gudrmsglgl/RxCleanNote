package com.cleannote.espresso.assertion

import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matchers

interface BaseAssertion {
    val viewInteraction: ViewInteraction

    fun isDisplayed(){
        viewInteraction.check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()
        ))
    }

    fun isNotDisplayed(){
        viewInteraction.check(ViewAssertions.matches(
            Matchers.not(ViewMatchers.isDisplayed())
        ))
    }

    fun doesNotExist(){
        viewInteraction.check(ViewAssertions.doesNotExist())
    }

    fun isVisible(){
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(
                    ViewMatchers.Visibility.VISIBLE
                )
            )
        )
    }

    fun isInVisible(){
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(
                    ViewMatchers.Visibility.INVISIBLE
                )
            )
        )
    }

    fun isGone(){
        viewInteraction.check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(
                    ViewMatchers.Visibility.GONE
                )
            )
        )
    }
}