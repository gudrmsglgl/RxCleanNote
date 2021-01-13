package com.cleannote.extension

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.annotation.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
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


@ColorInt
fun resolveColor(
    context: Context,
    @ColorRes res: Int? = null,
    @AttrRes attr: Int? = null,
    fallback: (() -> Int)? = null
): Int {
    if (attr != null) {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        try {
            val result = a.getColor(0, 0)
            if (result == 0 && fallback != null) {
                return fallback()
            }
            return result
        } finally {
            a.recycle()
        }
    }
    return ContextCompat.getColor(context, res ?: 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}