package com.cleannote.notedetail.holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemAttachImageBinding
import com.cleannote.model.NoteImageUiModel

class EditImageHolder(
    val binding: ItemAttachImageBinding,
    val requestManager: RequestManager
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NoteImageUiModel){
        binding.apply{
            reqManager = requestManager
            imageModel = item
        }
    }
}