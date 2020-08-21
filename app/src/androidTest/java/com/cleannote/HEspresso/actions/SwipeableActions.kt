package com.cleannote.HEspresso.actions

import androidx.test.espresso.action.*

interface SwipeableActions: BaseActions {

    fun swipeLeft() {
        viewInteraction.perform(ViewActions.swipeLeft())
    }

    fun swipeRight(){
        viewInteraction.perform(ViewActions.swipeRight())
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