package com.cleannote.notelist.dialog

import com.cleannote.app.R
import com.cleannote.common.dialog.BaseDialog
import com.cleannote.common.dialog.DeleteDialog
import com.cleannote.model.NoteUiModel

class ListDeleteDialog(dialog: DeleteDialog): BaseDialog by dialog {
    fun showDeleteDialog(
        notes: List<NoteUiModel>
    ) = makeDefaultDialog()
        .message(text = deleteTitle(notes))

    private fun deleteTitle(
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