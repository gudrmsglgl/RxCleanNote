package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.HEspresso.button.NFloatingButton
import com.cleannote.HEspresso.dialog.NDDeleteDialog
import com.cleannote.HEspresso.dialog.NErrorDialog
import com.cleannote.HEspresso.dialog.NLNewNoteDialog
import com.cleannote.HEspresso.recycler.NRecyclerView
import com.cleannote.HEspresso.text.NTextView
import com.cleannote.HEspresso.text.NToast
import com.cleannote.HEspresso.toolbar.NMultiDeleteToolbar
import com.cleannote.HEspresso.toolbar.NSearchToolbar
import com.cleannote.app.R

object NoteListScreen: BaseScreen<NoteListScreen>() {

    val recyclerView = NRecyclerView(R.id.recycler_view)
    val noDataTextView = NTextView(withId(R.id.tv_no_data))
    val searchToolbar = NSearchToolbar(withId(R.id.search_toolbar))
    val multiDeleteToolbar = NMultiDeleteToolbar(withId(R.id.toolbar_multi_delete))
    val insertBtn = NFloatingButton(withId(R.id.add_new_note_fab))
    val newNoteDialog = NLNewNoteDialog(withId(R.layout.md_dialog_stub_input))
    val deleteDialog = NDDeleteDialog(withId(R.layout.md_dialog_base))
    val deleteSuccessToast = NToast(withText(R.string.deleteSuccessMsg))
    val deleteErrorToast = NToast(withText(R.string.deleteErrorMsg))
    val errorDialog = NErrorDialog(withId(R.layout.md_dialog_base))
}