package com.cleannote.common.dialog

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog

interface BaseDialog {
    val context: Context
    fun makeDefaultDialog(): MaterialDialog
    fun showToast(@StringRes idRes: Int) {
        Toast.makeText(context, context.resources.getString(idRes), Toast.LENGTH_SHORT).show()
    }
}
