package com.cleannote.notedetail.edit.dialog

import com.cleannote.app.R
import com.cleannote.common.dialog.BaseDialog
import com.cleannote.common.dialog.DeleteDialog

class EditDeleteDialog(dialog: DeleteDialog): BaseDialog by dialog {

    fun showNoteDeleteDialog() = makeDefaultDialog()
        .message(R.string.delete_message)

    fun showImageDeleteDialog() = makeDefaultDialog()
        .message(R.string.delete_image_message)

}