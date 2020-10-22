package com.cleannote.notelist.holder

import com.cleannote.app.databinding.ItemSingleDeleteNoteBinding
import com.cleannote.model.NoteUiModel
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.subjects.PublishSubject

class SingleDeleteHolder(val binding: ItemSingleDeleteNoteBinding): BaseHolder<NoteUiModel>(binding) {

    override fun bind(item: NoteUiModel, position: Int, clickSubject: PublishSubject<NoteUiModel>) {
        binding.menuDeleteContainer.apply {
            clicks()
                .map { item }
                .subscribe( clickSubject  )
        }
    }
}