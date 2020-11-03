package com.cleannote.notedetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.model.NoteImageUiModel
import com.cleannote.notedetail.holder.AttachImageHolder

class AttachImagesAdapter(
    val requestManager: RequestManager
): ListAdapter<NoteImageUiModel, AttachImageHolder>(ImageDiffCallback) {

    object ImageDiffCallback: DiffUtil.ItemCallback<NoteImageUiModel>(){
        override fun areItemsTheSame(oldItem: NoteImageUiModel, newItem: NoteImageUiModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NoteImageUiModel, newItem: NoteImageUiModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachImageHolder {
        return AttachImageHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_attach_image, parent, false),
            requestManager
        )
    }

    override fun onBindViewHolder(holder: AttachImageHolder, position: Int) {
        holder.bind(currentList[position])
    }
}