package com.cleannote.HEspresso.recycler

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.HEspresso.assertion.BaseAssertion
import com.cleannote.HEspresso.view.NBaseView
import com.cleannote.notelist.NoteListAdapter
import com.cleannote.test.util.RecyclerViewMatcher
import org.hamcrest.Matcher
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.core.Is.`is`

class NRecyclerView(@IdRes val idRes: Int): NBaseView<NRecyclerView>(withId(idRes)),
    RecyclerAdapterAssertion, RecyclerActions {

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