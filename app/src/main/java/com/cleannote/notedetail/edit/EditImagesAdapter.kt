package com.cleannote.notedetail.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.model.NoteImageUiModel
import com.cleannote.notedetail.holder.EditImageHolder

class EditImagesAdapter(
    val requestManager: RequestManager
): ListAdapter<NoteImageUiModel, EditImageHolder>(ImageDiffCallback) {

    object ImageDiffCallback: DiffUtil.ItemCallback<NoteImageUiModel>(){
        override fun areItemsTheSame(oldItem: NoteImageUiModel, newItem: NoteImageUiModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NoteImageUiModel, newItem: NoteImageUiModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditImageHolder {
        return EditImageHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_attach_image, parent, false),
            requestManager
        )
    }

    override fun onBindViewHolder(holder: EditImageHolder, position: Int) {
        holder.bind(currentList[position])
    }
}