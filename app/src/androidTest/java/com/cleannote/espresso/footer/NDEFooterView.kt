package com.cleannote.espresso.footer

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.app.R
import com.cleannote.espresso.menu.NDEPopupMenu
import com.cleannote.espresso.recycler.edit.NImgRecyclerView
import com.cleannote.espresso.recycler.list.NRecyclerView
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NDEFooterView(matcher: Matcher<View>): NBaseView<NDEFooterView>(matcher) {
    val popupMenu = NDEPopupMenu(ViewMatchers.withId(R.id.detail_footer_popup))
    val tvNoImages = NTextView(ViewMatchers.withId(R.id.tv_footer_no_images))
    val imageRcv = NImgRecyclerView(R.id.rcv_images)
}