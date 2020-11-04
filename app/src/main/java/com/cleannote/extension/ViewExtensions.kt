package com.cleannote.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupMenu
import com.cleannote.app.R


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.isVisible(): Boolean {
    return (visibility == VISIBLE)
}

fun View.fadeIn() {
    //val animationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    apply {
        visible()
        alpha = 0f
        animate()
            .alpha(1f)
            .setDuration(100L)
            .setListener(null)
    }
}

fun View.fadeOut(){
    //val animationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
    apply {
        animate()
            .alpha(0f)
            .setDuration(100L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    invisible()
                }
            })
    }
}

fun EditText.enableEdit(){
    isFocusable = true
    isFocusableInTouchMode = true
    isCursorVisible = true
    requestFocus()
}

fun EditText.disableEdit(){
    isFocusable = false
    isFocusableInTouchMode = false
    isCursorVisible = false
    clearFocus()
}

/**
 * Use everywhere except from Activity (Custom View, Fragment, Dialogs, DialogFragments).
 */

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun PopupMenu.visibleIcon(context: Context){
    val menu = this.menu
    if (hasIcon(menu)){
        for (i in 0 until menu.size()){
            insertMenuItemIcon(context, menu.getItem(i))
        }
    }
}

/**
 * @return true if the menu has at least one MenuItem with an icon.
 */
private fun hasIcon(menu: Menu): Boolean{
    for (i in 0 until menu.size()){
        if (menu.getItem(i).icon != null) return true
    }
    return false
}

/**
 * Converts the given MenuItem's title into a Spannable containing both its icon and title.
 */
private fun insertMenuItemIcon(context: Context, menuItem: MenuItem) {
    var icon: Drawable = menuItem.icon

    // If there's no icon, we insert a transparent one to keep the title aligned with the items
    // which do have icons.
    if (icon == null) icon = ColorDrawable(Color.TRANSPARENT)

    val iconSize = context.resources.getDimensionPixelSize(R.dimen.menu_item_icon_size)
    icon.setBounds(0, 0, iconSize, iconSize)
    val imageSpan = ImageSpan(icon)

    // Add a space placeholder for the icon, before the title.
    val ssb = SpannableStringBuilder("       " + menuItem.title)

    // Replace the space placeholder with the icon.
    ssb.setSpan(imageSpan, 1, 2, 0)
    menuItem.title = ssb
    // Set the icon to null just in case, on some weird devices, they've customized Android to display
    // the icon in the menu... we don't want two icons to appear.
    menuItem.icon = null
}