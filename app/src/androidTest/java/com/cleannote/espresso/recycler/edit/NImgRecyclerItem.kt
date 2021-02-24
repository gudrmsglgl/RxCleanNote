package com.cleannote.espresso.recycler.edit

import androidx.annotation.IdRes
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.app.R
import com.cleannote.espresso.image.NEImageView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.test.util.RecyclerViewMatcher

class NImgRecyclerItem(
    @IdRes private val idRes: Int,
    private val position: Int
): NBaseView<NImgRecyclerItem>(
    RecyclerViewMatcher(idRes).atPositionOnView(position, -1)
) {
    val img = NEImageView(withId(R.id.attach_image))
}