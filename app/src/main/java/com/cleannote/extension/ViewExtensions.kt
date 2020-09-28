package com.cleannote.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

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