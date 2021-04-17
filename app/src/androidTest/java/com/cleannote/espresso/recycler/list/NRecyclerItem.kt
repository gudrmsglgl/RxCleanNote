package com.cleannote.espresso.recycler.list

import androidx.annotation.IdRes
import com.cleannote.app.R
import com.cleannote.espresso.actions.SwipeableActions
import com.cleannote.espresso.check.NCheckBox
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.test.util.RecyclerViewMatcher

@Suppress("UNCHECKED_CAST")
class NRecyclerItem<out T>(
    @IdRes private val idRes: Int,
    private val position: Int
) : SwipeableActions,
    NBaseView<NRecyclerItem<T>>(RecyclerViewMatcher(idRes).atPositionOnView(position, -1)) {
    val checkBox = NCheckBox(RecyclerViewMatcher(idRes).atPositionOnView(position, R.id.checkbox_delete))
    val itemTitle: NTextView = NTextView(
        RecyclerViewMatcher(idRes).atPositionOnView(position, R.id.tv_title_list)
    )
    val swipeDeleteMenu = NRcvDeleteSwipeMenu(
        idRes,
        position,
        RecyclerViewMatcher(idRes).atPositionOnView(position, R.id.swipe_menu_delete)
    )
}
