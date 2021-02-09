package com.cleannote.common.dialog

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.cleannote.app.R

class DeleteDialog(override val context: Context): BaseDialog {

    override fun makeDefaultDialog() = MaterialDialog(context)
        .show {
            title(R.string.delete_title)
            positiveButton(R.string.dialog_ok)
            negativeButton(R.string.dialog_cancel){
                showToast(R.string.deleteCancelMsg)
                dismiss()
            }
            cancelable(false)
        }

}