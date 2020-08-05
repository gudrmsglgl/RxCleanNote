package com.cleannote.HEspresso.recycler

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.assertion.BaseAssertion
import com.cleannote.notelist.NoteListAdapter

class NRecyclerView(@IdRes val idRes: Int): BaseAssertion,
    RecyclerAdapterAssertion, RecyclerActions {

    override val viewInteraction: ViewInteraction
        get() = onView(withId(idRes))

    operator fun invoke(function: NRecyclerView.() -> Unit){
        function(this)
    }

    inline fun <reified T: NRecyclerItem<*>> childAt(position: Int, function: T.() -> Unit){
        function(NRecyclerItem<T>(this.idRes, position) as T)
    }

    inline fun <reified T: NRecyclerItem<*>> firstItem(function: T.() -> Unit){
        childAt(0, function)
    }

    inline fun <reified T: NRecyclerItem<*>> lastItem(function: T.() -> Unit){
        childAt(getSize()-1, function)
    }
}