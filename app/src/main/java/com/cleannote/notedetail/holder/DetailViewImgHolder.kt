package com.cleannote.notedetail.holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemDetailViewImgBinding
import com.cleannote.model.NoteImageUiModel

class DetailViewImgHolder(
    val binding: ItemDetailViewImgBinding,
    val requestManager: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NoteImageUiModel) {
        binding.apply {
            reqManager = requestManager
            imageModel = item
        }
    }
}
