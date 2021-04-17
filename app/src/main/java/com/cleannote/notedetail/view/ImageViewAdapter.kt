package com.cleannote.notedetail.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.model.NoteImageUiModel
import com.cleannote.notedetail.holder.DetailViewImgHolder

class ImageViewAdapter(
    private val requestManager: RequestManager
) : ListAdapter<NoteImageUiModel, DetailViewImgHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<NoteImageUiModel>() {
        override fun areItemsTheSame(
            oldItem: NoteImageUiModel,
            newItem: NoteImageUiModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: NoteImageUiModel,
            newItem: NoteImageUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewImgHolder {
        return DetailViewImgHolder(
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_detail_view_img, parent,
                false
            ),
            requestManager = requestManager
        )
    }

    override fun onBindViewHolder(holder: DetailViewImgHolder, position: Int) {
        holder.bind(currentList[position])
    }
}
