package com.cleannote.HEspresso.recycler

import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import com.cleannote.HEspresso.assertion.AdapterAssertion
import com.cleannote.HEspresso.assertion.BaseAssertion
import com.cleannote.HEspresso.recycler.RecyclerViewAdapterSizeMatcher

interface RecyclerAdapterAssertion: AdapterAssertion {

    fun hasSize(expectedSize: Int) {
        viewInteraction.check(
            ViewAssertions.matches(
                RecyclerViewAdapterSizeMatcher(expectedSize)
            )
        )
    }

}