package com.cleannote.espresso.actions

import androidx.test.espresso.action.*

interface SwipeableActions: BaseActions {

    fun swipeLeft(){
        viewInteraction.perform(GeneralSwipeAction(
            Swipe.SLOW,
            GeneralLocation.CENTER,
            GeneralLocation.CENTER_LEFT,
            Press.FINGER
        ))
    }

    fun swipeUp() {
        viewInteraction.perform(GeneralSwipeAction(
            Swipe.SLOW,
            GeneralLocation.BOTTOM_CENTER,
            GeneralLocation.TOP_CENTER,
            Press.FINGER
        ))
    }

    fun swipeDown() {
        viewInteraction.perform(GeneralSwipeAction(
            Swipe.SLOW,
            GeneralLocation.TOP_CENTER,
            GeneralLocation.BOTTOM_CENTER,
            Press.FINGER
        ))
    }
}