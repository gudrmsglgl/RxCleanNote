package com.cleannote.HEspresso

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.recycler.NRecyclerView
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.app.R

object NoteListScreen {
    val recyclerView = NRecyclerView(R.id.recycler_view)
    val noDataTextView = NTextView(withId(R.id.tv_no_data))

    operator fun invoke(function: NoteListScreen.() -> Unit) {
        function(this)
    }
}