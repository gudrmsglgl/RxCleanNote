package com.cleannote.espresso.recycler

import androidx.test.espresso.assertion.ViewAssertions
import com.cleannote.espresso.assertion.AdapterAssertion

interface RecyclerAdapterAssertion: AdapterAssertion {

    fun hasSize(expectedSize: Int) {
        viewInteraction.check(
            ViewAssertions.matches(
                RecyclerViewAdapterSizeMatcher(expectedSize)
            )
        )
    }
}