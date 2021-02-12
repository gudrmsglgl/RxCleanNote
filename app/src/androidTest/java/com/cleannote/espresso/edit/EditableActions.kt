package com.cleannote.espresso.edit

import androidx.test.espresso.action.ViewActions
import com.cleannote.espresso.actions.BaseActions
import com.cleannote.espresso.text.TextViewAssertions

interface EditableActions: BaseActions, TextViewAssertions {
    fun typeText(text: String){
        viewInteraction.perform(ViewActions.typeText(text))
    }
    fun replaceText(text: String) {
        viewInteraction.perform(ViewActions.replaceText(text))
    }
}