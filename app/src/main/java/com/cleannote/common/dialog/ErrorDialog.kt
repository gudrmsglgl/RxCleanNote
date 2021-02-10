package com.cleannote.common.dialog

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.cleannote.app.R

class ErrorDialog(
    override val context: Context,
    private val viewLifeCycleOwner: LifecycleOwner
): BaseDialog {

    override fun makeDefaultDialog(): MaterialDialog = MaterialDialog(context)
        .title(R.string.dialog_title_error)

    fun showDialog(errMessage: String) = makeDefaultDialog()
        .show {
            message(text = errMessage)
            positiveButton {
                dismiss()
            }
            lifecycleOwner(viewLifeCycleOwner)
        }
}