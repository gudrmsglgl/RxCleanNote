package com.cleannote.espresso.menu

import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matchers.*


class MenuItem(private val menuPos: Int){
    private val dataInteraction: DataInteraction
        get() = onData(anything())
            .atPosition(menuPos)
            .inRoot(isPlatformPopup())

    operator fun invoke(func: MenuItem.() -> Unit){
        func.invoke(this)
    }

    private fun click() = dataInteraction.perform(ViewActions.click())

    fun isDisplayed() = dataInteraction.check(ViewAssertions.matches(
        ViewMatchers.isDisplayed()
    ))
}