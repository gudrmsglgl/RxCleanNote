package com.cleannote.notelist.holder

import com.cleannote.app.databinding.ItemSingleDeleteNoteBinding
import com.cleannote.model.NoteUiModel
import io.reactivex.rxjava3.subjects.PublishSubject

class SingleDeleteHolder(val binding: ItemSingleDeleteNoteBinding): BaseHolder<NoteUiModel>(binding) {

    override fun bind(item: NoteUiModel, position: Int, clickSubject: PublishSubject<NoteUiModel>) {
        binding.apply {
            holder = this@SingleDeleteHolder
            noteUiModel = item
            selectPosition = position
            setClickSubject(clickSubject)
        }
    }
}