package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.dialog.NErrorDialog
import com.cleannote.HEspresso.dialog.NLNewNoteDialog
import com.cleannote.app.R

object MainActivityScreen: BaseScreen<MainActivityScreen>() {
    val noteListScreen = NoteListScreen
    val noteDetailScreen = DetailNoteScreen
    val newNoteDialog = NLNewNoteDialog(withId(R.layout.md_dialog_stub_input))
    val errorDialog = NErrorDialog(withId(R.layout.md_dialog_base))
}