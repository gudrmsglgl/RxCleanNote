package com.cleannote.HEspresso.edit

import androidx.test.espresso.action.ViewActions
import com.cleannote.HEspresso.actions.BaseActions

interface EditableActions: BaseActions {
    fun typeText(text: String){
        viewInteraction.perform(ViewActions.typeText(text))
    }
    fun replaceText(text: String) {
        viewInteraction.perform(ViewActions.replaceText(text))
    }
}