package com.cleannote.notelist.holder

import androidx.core.view.isVisible
import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemNoteListBinding
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.SubjectManager
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks

class NoteViewHolder(
    val binding: ItemNoteListBinding,
    private val requestManager: RequestManager
): BaseHolder<NoteUiModel>(binding){

    override fun bind(
        item: NoteUiModel,
        position: Int,
        subjectManager: SubjectManager
    ) {
        binding.apply {
            glideReqManager = requestManager
            noteUiModel = item

            if (item.mode == MultiDefault) checkboxDelete.isChecked = false
            else if (item.mode == MultiSelect) checkboxDelete.isChecked = true

            swipeMenuDelete
                .clicks()
                .filter { isClamped(this@NoteViewHolder) && swipeMenuDelete.isVisible }
                .map { item }
                .subscribe(subjectManager.deleteClickSubject)


            itemNote
                .clicks()
                .map { item }
                .doOnNext{
                    if (it.mode != Default)
                        checkboxDelete.isChecked = !checkboxDelete.isChecked
                }
                .subscribe(subjectManager.clickNoteSubject)

            itemNote
                .longClicks { true }
                .subscribe(subjectManager.longClickSubject)

        }
    }

    private fun isClamped(holder: NoteViewHolder) = holder.itemView.tag as? Boolean ?: false

}