package com.cleannote.HEspresso.recycler

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.assertion.BaseAssertion
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.app.R
import com.cleannote.test.util.RecyclerViewMatcher

@Suppress("UNCHECKED_CAST")
class NRecyclerItem<out T>(@IdRes private val idRes: Int, private val position: Int): BaseAssertion {

    override val viewInteraction: ViewInteraction
        get() = onView(RecyclerViewMatcher(idRes).atPositionOnView(position,-1))

    val itemTitle: NTextView = NTextView(
        RecyclerViewMatcher(idRes).atPositionOnView(position, R.id.note_title)
    )

    operator fun invoke(function: T.()-> Unit){
        function(this as T)
    }

}