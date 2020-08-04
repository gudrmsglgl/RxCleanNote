package com.cleannote.HEspresso.recycler

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.assertion.BaseAssertion

class NRecyclerView(@IdRes val idRes: Int): BaseAssertion,
    RecyclerAdapterAssertion {

    override val viewInteraction: ViewInteraction
        get() = onView(withId(idRes))

    operator fun invoke(function: NRecyclerView.() -> Unit){
        function(this)
    }

}