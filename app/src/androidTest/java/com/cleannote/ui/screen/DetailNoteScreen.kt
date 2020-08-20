package com.cleannote.ui.screen

import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.HEspresso.edit.NDEditText
import com.cleannote.HEspresso.scroll.NDScrollView
import com.cleannote.app.R

object DetailNoteScreen: BaseScreen<DetailNoteScreen>() {
    val noteTitle = NDEditText(ViewMatchers.withId(R.id.note_title))
    val scrollview = NDScrollView(ViewMatchers.withId(R.id.note_scroll_view))
}