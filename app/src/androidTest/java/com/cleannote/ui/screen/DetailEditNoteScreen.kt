package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.espresso.edit.NDEditText
import com.cleannote.espresso.scroll.NDScrollView
import com.cleannote.espresso.text.NToast
import com.cleannote.espresso.toolbar.NDToolbar
import com.cleannote.app.R
import com.cleannote.espresso.dialog.NDDeleteDialog
import com.cleannote.espresso.dialog.NErrorDialog
import com.cleannote.espresso.footer.NDEFooterView

object DetailEditNoteScreen: BaseScreen<DetailEditNoteScreen>() {
    val toolbar = NDToolbar(ViewMatchers.withId(R.id.detail_toolbar))
    val editTitle = NDEditText(ViewMatchers.withId(R.id.edit_title))
    val scrollview = NDScrollView(ViewMatchers.withId(R.id.edit_body))
    val footer = NDEFooterView(ViewMatchers.withId(R.id.footer))
    val deleteDialog = NDDeleteDialog(ViewMatchers.withId(R.layout.md_dialog_base))
    val updateSuccessToast = NToast(withText(R.string.updateSuccessMsg))
    val errorDialog = NErrorDialog(ViewMatchers.withId(R.layout.md_dialog_base))
    val deleteSuccessToast = NToast(withText(R.string.deleteSuccessMsg))
}