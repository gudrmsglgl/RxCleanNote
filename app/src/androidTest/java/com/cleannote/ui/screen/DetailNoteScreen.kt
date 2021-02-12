package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.espresso.edit.NDEditText
import com.cleannote.espresso.scroll.NDScrollView
import com.cleannote.espresso.text.NToast
import com.cleannote.espresso.toolbar.NDToolbar
import com.cleannote.app.R

object DetailNoteScreen: BaseScreen<DetailNoteScreen>() {
    val toolbar = NDToolbar(ViewMatchers.withId(R.id.detail_toolbar))
    val noteTitle = NDEditText(ViewMatchers.withId(R.id.edit_title))
    val scrollview = NDScrollView(ViewMatchers.withId(R.id.note_scroll_view))
    val updateSuccessToast = NToast(withText(R.string.updateSuccessMsg))
    val updateErrorToast = NToast(withText(R.string.updateErrorMsg))
    val deleteSuccessToast = NToast(withText(R.string.deleteSuccessMsg))
    val deleteErrorToast = NToast(withText(R.string.deleteErrorMsg))
}