package com.cleannote.HEspresso

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.button.NFloatingButton
import com.cleannote.HEspresso.dialog.NLNewNoteDialog
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