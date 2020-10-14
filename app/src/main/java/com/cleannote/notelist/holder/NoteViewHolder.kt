package com.cleannote.notelist.holder

import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemNoteListBinding
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.notelist.NoteListViewModel
import io.reactivex.rxjava3.subjects.PublishSubject

class NoteViewHolder(
    val binding: ItemNoteListBinding,
    private val requestManager: RequestManager,
    val viewModel: NoteListViewModel
): BaseHolder<NoteUiModel>(binding){
    override fun bind(item: NoteUiModel, position: Int, clickSubject: PublishSubject<NoteUiModel>) {
        binding.apply {
            holder = this@NoteViewHolder
            selectPosition = position
            glideReqManager = requestManager
            noteUiModel = item
            setClickSubject(clickSubject)
        }
    }
}