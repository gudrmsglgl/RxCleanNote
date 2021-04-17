package com.cleannote.espresso.recycler.list

import androidx.annotation.IdRes
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.espresso.recycler.RecyclerActions
import com.cleannote.espresso.recycler.RecyclerAdapterAssertion
import com.cleannote.espresso.view.NBaseView

class NRecyclerView(@IdRes val idRes: Int) :
    NBaseView<NRecyclerView>(withId(idRes)),
    RecyclerAdapterAssertion,
    RecyclerActions {

    inline fun <reified T : NRecyclerItem<*>> childAt(position: Int, function: T.() -> Unit) {
        function(NRecyclerItem<T>(this.idRes, position) as T)
    }

    inline fun <reified T : NRecyclerItem<*>> firstItem(function: T.() -> Unit) {
        childAt(0, function)
    }

    inline fun <reified T : NRecyclerItem<*>> visibleLastItem(function: T.() -> Unit) {
        childAt(getLastVisiblePosition(), function)
    }
}
