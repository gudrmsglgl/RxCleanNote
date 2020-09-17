package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.button.NFloatingButton
import com.cleannote.HEspresso.recycler.NRecyclerView
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.HEspresso.toolbar.NLToolbar
import com.cleannote.app.R

object NoteListScreen: BaseScreen<NoteListScreen>() {

    val recyclerView = NRecyclerView(R.id.recycler_view)
    val noDataTextView = NTextView(withId(R.id.tv_no_data))
    val toolbar = NLToolbar(withId(R.id.searchview_toolbar))
    val insertBtn = NFloatingButton(withId(R.id.add_new_note_fab))

}