package com.cleannote.HEspresso

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.recycler.NRecyclerView
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.HEspresso.toolbar.NLToolbar
import com.cleannote.app.R

object NoteListScreen {

    val recyclerView = NRecyclerView(R.id.recycler_view)
    val noDataTextView = NTextView(withId(R.id.tv_no_data))
    val toolbar = NLToolbar(withId(R.id.searchview_toolbar))

    operator fun invoke(function: NoteListScreen.() -> Unit) {
        function(this)
    }

    fun idle(duration: Long = 1000L) {
        onView(ViewMatchers.isRoot()).perform(object : ViewAction {
            override fun getDescription() = "Idle for $duration milliseconds"

            override fun getConstraints() = ViewMatchers.isAssignableFrom(View::class.java)

            override fun perform(uiController: UiController?, view: View?) {
                uiController?.loopMainThreadForAtLeast(duration)
            }
        })
    }
}