package com.cleannote.espresso.search

import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import com.cleannote.espresso.actions.BaseActions

interface NSearchActions: BaseActions {

    fun searchText(text: String?){
        viewInteraction
            .perform(typeText(text), closeSoftKeyboard())
    }

}