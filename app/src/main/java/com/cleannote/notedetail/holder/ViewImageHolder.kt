package com.cleannote.notedetail.holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemViewImageBinding
import com.cleannote.model.NoteImageUiModel

class ViewImageHolder(
    val binding: ItemViewImageBinding,
    val requestManager: RequestManager
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NoteImageUiModel){
        binding.apply {
            reqManager = requestManager
            imageModel = item
        }
    }

}