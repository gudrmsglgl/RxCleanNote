package com.cleannote.espresso.menu

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NDEPopupMenu(matcher: Matcher<View>): NBaseView<NDEPopupMenu>(matcher) {
    val cameraMenu = MenuItem(menuPos = 0)
    val albumMenu = MenuItem(menuPos = 1)
    val linkMenu = MenuItem(menuPos = 2)
}