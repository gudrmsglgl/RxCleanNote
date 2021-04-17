package com.cleannote.common.dialog

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.cleannote.app.R

class DeleteDialog(
    override val context: Context,
    private val viewLifeCycleOwner: LifecycleOwner
) : BaseDeleteDialog {

    override fun makeDefaultDialog() = MaterialDialog(context)
        .show {
            title(R.string.delete_title)
            positiveButton(R.string.dialog_ok)
            negativeButton(R.string.dialog_cancel) {
                showToast(R.string.deleteCancelMsg)
                dismiss()
            }
            cancelable(false)
            lifecycleOwner(viewLifeCycleOwner)
        }

    override fun showDialog(message: String): MaterialDialog = makeDefaultDialog()
        .message(text = message)
}
