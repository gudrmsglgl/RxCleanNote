package com.cleannote.espresso.toolbar.list

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.app.R
import com.cleannote.espresso.dialog.NDDeleteDialog
import com.cleannote.espresso.image.NImageView
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NDToolbar(matcher: Matcher<View>) : NBaseView<NDToolbar>(matcher) {
    val primaryMenu = NImageView(ViewMatchers.withId(R.id.left_icon))
    val toolbarTitle = NTextView(ViewMatchers.withId(R.id.tool_bar_title))
    val secondMenu = NImageView(ViewMatchers.withId(R.id.right_icon))
    val deleteDialog = NDDeleteDialog(ViewMatchers.withId(R.layout.md_dialog_base))
}
