package com.cleannote.HEspresso

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.HEspresso.dialog.NLNewNoteDialog
import com.cleannote.app.R

object MainActivityScreen: BaseScreen<MainActivityScreen>() {
    val newNoteDialog = NLNewNoteDialog(withId(R.layout.md_dialog_stub_input))
}