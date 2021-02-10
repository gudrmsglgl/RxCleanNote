package com.cleannote.common.dialog

import com.afollestad.materialdialogs.MaterialDialog

interface BaseDeleteDialog: BaseDialog {
    fun showDialog(message: String): MaterialDialog
}