package com.cleannote.HEspresso

import com.cleannote.HEspresso.recycler.NRecyclerView
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.app.R

object NoteListScreen {
    val recyclerView =
        NRecyclerView(R.id.recycler_view)
    val nonDataTestView = NTextView(R.id.tv_no_data)

    operator fun invoke(function: NoteListScreen.() -> Unit) {
        function(this)
    }
}