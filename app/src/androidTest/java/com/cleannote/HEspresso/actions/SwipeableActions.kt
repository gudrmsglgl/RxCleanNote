package com.cleannote.HEspresso.actions

import androidx.test.espresso.action.ViewActions

interface SwipeableActions: BaseActions {

    fun swipeLeft() {
        viewInteraction.perform(ViewActions.swipeLeft())
    }

    fun swipeRight(){
        viewInteraction.perform(ViewActions.swipeRight())
    }

}