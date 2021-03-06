package com.cleannote.notelist.holder

import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemCheckNoteListBinding
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.SubjectManager
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.checkedChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import java.util.concurrent.TimeUnit

class NoteCheckViewHolder(
    private val binding: ItemCheckNoteListBinding,
    private val requestManager: RequestManager
): BaseHolder<NoteUiModel>(binding) {

    override fun bind(
        item: NoteUiModel,
        position: Int,
        subjectManager: SubjectManager
    ) {
        binding.apply {
            noteUiModel = item
            glideReqManager = requestManager
        }
        with (binding) {
            initCheckNote(item)

            val clickSource = itemNote.clicks()
                .doOnNext { checkboxDelete.isChecked = !checkboxDelete.isChecked }

            Observable.combineLatest(
                clickSource,
                checkboxDelete.checkedChanges().skipInitialValue(),
                BiFunction { _: Unit, isChecked: Boolean ->
                    isChecked
                })
                .debounce(1000L, TimeUnit.MILLISECONDS)
                .map {
                    item to checkboxDelete.isChecked
                }
                .subscribe(subjectManager.checkNoteSubject)

        }
    }

    private fun ItemCheckNoteListBinding.initCheckNote(item: NoteUiModel){
        if (item.mode == NoteMode.MultiDefault) {
            checkboxDelete.isChecked = false
        }
        else if (item.mode == NoteMode.MultiSelect) {
            checkboxDelete.isChecked = true
        }
    }

}