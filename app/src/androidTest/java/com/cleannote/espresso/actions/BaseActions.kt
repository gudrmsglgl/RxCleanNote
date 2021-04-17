package com.cleannote.espresso.actions

import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.*

interface BaseActions {
    val viewInteraction: ViewInteraction

    fun click(location: GeneralLocation = GeneralLocation.VISIBLE_CENTER) {
        viewInteraction.perform(
            GeneralClickAction(
                Tap.SINGLE, location, Press.FINGER,
                InputDevice.SOURCE_UNKNOWN, MotionEvent.BUTTON_PRIMARY
            )
        )
    }

    fun longClick(location: GeneralLocation = GeneralLocation.VISIBLE_CENTER) {
        viewInteraction.perform(
            GeneralClickAction(
                Tap.LONG, location, Press.FINGER,
                InputDevice.SOURCE_UNKNOWN, MotionEvent.BUTTON_PRIMARY
            )
        )
    }

    fun pressBack() {
        viewInteraction.perform(
            ViewActions.pressBack()
        )
    }
}
