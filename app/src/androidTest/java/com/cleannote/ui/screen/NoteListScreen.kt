package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.espresso.button.NFloatingButton
import com.cleannote.espresso.dialog.NDDeleteDialog
import com.cleannote.espresso.dialog.NErrorDialog
import com.cleannote.espresso.dialog.NLNewNoteDialog
import com.cleannote.espresso.recycler.NRecyclerView
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.text.NToast
import com.cleannote.espresso.toolbar.NMultiDeleteToolbar
import com.cleannote.espresso.toolbar.NSearchToolbar
import com.cleannote.app.R
import com.cleannote.espresso.dialog.NLFilterDialog

object NoteListScreen: BaseScreen<NoteListScreen>() {

    val recyclerView = NRecyclerView(R.id.recycler_view)
    val noDataTextView = NTextView(withId(R.id.tv_no_data))
    val searchToolbar = NSearchToolbar(withId(R.id.search_toolbar))
    val multiDeleteToolbar = NMultiDeleteToolbar(withId(R.id.toolbar_multi_delete))
    val insertBtn = NFloatingButton(withId(R.id.add_new_note_fab))
    val newNoteDialog = NLNewNoteDialog(withId(R.layout.md_dialog_stub_input))
    val filterDialog = NLFilterDialog(withId(R.id.filter_dialog))
    val deleteDialog = NDDeleteDialog(withId(R.layout.md_dialog_base))
    val deleteSuccessToast = NToast(withText(R.string.deleteSuccessMsg))
    val deleteErrorToast = NToast(withText(R.string.deleteErrorMsg))
    val errorDialog = NErrorDialog(withId(R.layout.md_dialog_base))
}