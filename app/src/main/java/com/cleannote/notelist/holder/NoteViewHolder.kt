package com.cleannote.notelist.holder

import androidx.core.view.isVisible
import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemNoteListBinding
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.SubjectManager
import com.cleannote.notelist.swipe.SwipeHelperCallback
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import com.jakewharton.rxbinding4.widget.checkedChanges

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

            //initChecked()

            swipeMenuDelete
                .clicks()
                .filter { isClamped(this@NoteViewHolder) && swipeMenuDelete.isVisible }
                .map { item }
                .subscribe(subjectManager.deleteClickSubject)


            itemNote
                .clicks()
                .map { item }
                .doOnNext{
                    if (checkboxDelete.isVisible) {
                        checkboxDelete.isChecked = !checkboxDelete.isChecked
                    }
                }
                .subscribe(subjectManager.clickNoteSubject)

            itemNote
                .longClicks { true }
                .subscribe(subjectManager.longClickSubject)

            checkboxDelete
                .checkedChanges()
                .skipInitialValue()
                .map { isChecked ->
                    position to isChecked
                }
                .subscribe(subjectManager.multiClickSubject)

        }
    }

    private fun isClamped(holder: NoteViewHolder) = holder.itemView.tag as? Boolean ?: false

    private fun initChecked() = with(binding){
        if (checkboxDelete.isVisible) {
            checkboxDelete.isChecked = false
        }
    }

}