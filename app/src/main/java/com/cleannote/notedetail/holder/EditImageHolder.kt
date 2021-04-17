package com.cleannote.notedetail.holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemAttachImageBinding
import com.cleannote.extension.rxbinding.singleClick
import com.cleannote.model.NoteImageUiModel
import io.reactivex.rxjava3.subjects.PublishSubject

class EditImageHolder(
    val binding: ItemAttachImageBinding,
    private val requestManager: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NoteImageUiModel, subject: PublishSubject<NoteImageUiModel>) {

        binding.apply {
            reqManager = requestManager
            imageModel = item
        }

        binding
            .icDelete
            .singleClick()
            .map { item }
            .subscribe(subject)
    }
}
