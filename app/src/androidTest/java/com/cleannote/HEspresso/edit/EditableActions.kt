package com.cleannote.HEspresso.edit

import androidx.test.espresso.action.ViewActions
import com.cleannote.HEspresso.actions.BaseActions
import com.cleannote.HEspresso.text.TextViewAssertions

interface EditableActions: BaseActions, TextViewAssertions {
    fun typeText(text: String){
        viewInteraction.perform(ViewActions.typeText(text))
    }
    fun replaceText(text: String) {
        viewInteraction.perform(ViewActions.replaceText(text))
    }
}