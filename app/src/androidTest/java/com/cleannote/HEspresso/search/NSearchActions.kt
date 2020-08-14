package com.cleannote.HEspresso.search

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.cleannote.HEspresso.actions.BaseActions
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf

interface NSearchActions: BaseActions {

    fun searchText(text: String?){
        viewInteraction
            .perform(typeText(text), closeSoftKeyboard())
    }

}