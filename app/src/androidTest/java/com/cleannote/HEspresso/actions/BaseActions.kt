package com.cleannote.HEspresso.actions

import android.view.InputDevice
import androidx.core.view.MotionEventCompat
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.*

interface BaseActions {
    val viewInteraction: ViewInteraction

    fun click(location: GeneralLocation = GeneralLocation.VISIBLE_CENTER) {
        viewInteraction.perform(
            GeneralClickAction(
                Tap.SINGLE, location, Press.FINGER,
                InputDevice.SOURCE_UNKNOWN, MotionEventCompat.BUTTON_PRIMARY
            )
        )
    }

    fun longClick(location: GeneralLocation = GeneralLocation.VISIBLE_CENTER) {
        viewInteraction.perform(
            GeneralClickAction(
                Tap.LONG, location, Press.FINGER,
                InputDevice.SOURCE_UNKNOWN, MotionEventCompat.BUTTON_PRIMARY
            )
        )
    }

    fun pressBack(){
        viewInteraction.perform(
            ViewActions.pressBack()
        )
    }

}