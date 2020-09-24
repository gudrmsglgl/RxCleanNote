package com.cleannote.ui.screen

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.cleannote.HEspresso.edit.NDEditText
import com.cleannote.HEspresso.scroll.NDScrollView
import com.cleannote.HEspresso.text.NToast
import com.cleannote.HEspresso.toolbar.NDToolbar
import com.cleannote.app.R

object DetailNoteScreen: BaseScreen<DetailNoteScreen>() {
    val toolbar = NDToolbar(ViewMatchers.withId(R.id.detail_toolbar))
    val noteTitle = NDEditText(ViewMatchers.withId(R.id.note_title))
    val scrollview = NDScrollView(ViewMatchers.withId(R.id.note_scroll_view))
    val updateSuccessToast = NToast(withText(R.string.updateSuccessMsg))
    val updateErrorToast = NToast(withText(R.string.updateErrorMsg))
    val deleteSuccessToast = NToast(withText(R.string.deleteSuccessMsg))
    val deleteErrorToast = NToast(withText(R.string.deleteErrorMsg))
}