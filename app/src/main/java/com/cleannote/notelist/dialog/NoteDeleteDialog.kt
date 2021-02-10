package com.cleannote.notelist.dialog

import com.afollestad.materialdialogs.MaterialDialog
import com.cleannote.app.R
import com.cleannote.common.dialog.BaseDeleteDialog
import com.cleannote.common.dialog.DeleteDialog
import com.cleannote.model.NoteUiModel

class NoteDeleteDialog(val dialog: DeleteDialog): BaseDeleteDialog by dialog {

    fun showDialog(notes: List<NoteUiModel>): MaterialDialog = showDialog(message(notes))

    private fun message(
        deleteMemos: List<NoteUiModel>
    ): String = when {
        deleteMemos.size == 1 -> {
            """
                |${deleteMemos[0].title}
                |메모를 삭제 하시겠습니까?
            """.trimMargin()
        }
        deleteMemos.size > 1 -> {
            context.resources.getString(R.string.delete_multi_select_message)
        }
        else -> ""
    }

}