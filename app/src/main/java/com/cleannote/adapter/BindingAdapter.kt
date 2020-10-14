package com.cleannote.adapter

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cleannote.app.R
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.NoteListAdapter
import com.cleannote.notelist.holder.BaseHolder
import com.cleannote.notelist.holder.NoteViewHolder
import com.cleannote.notelist.holder.SingleDeleteHolder
import com.cleannote.presentation.data.notelist.ListToolbarState.MultiSelectState
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks

object BindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["visible"])
    fun setVisible(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) VISIBLE else GONE
    }

    @JvmStatic
    @BindingAdapter(value = ["glideReqManager", "noteUiModel"])
    fun loadImage(
        imageView: ImageView,
        glideRequestManager: RequestManager,
        noteUiModel: NoteUiModel
    ){
        val image: Any =
            if (noteUiModel.images.isNullOrEmpty()) R.drawable.empty_holder
            else noteUiModel.images.get(0).img_path
        glideRequestManager
            .applyDefaultRequestOptions(
                RequestOptions.bitmapTransform(RoundedCorners(10))
            )
            .load(image)
            .thumbnail(0.1f)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["viewHolder", "selectedPos"])
    fun noteClick(
        view: View,
        holder: BaseHolder<NoteUiModel>,
        position: Int
    ){
        when(holder){
            is NoteViewHolder -> {
                holder.itemView.apply {

                    val noteUIModel = (holder.binding).noteUiModel
                    val viewModel = holder.viewModel
                    val subject = (holder.binding).clickSubject

                    when (noteUIModel!!.mode){
                        NoteMode.Default -> {
                            clicks()
                                .map { noteUIModel }
                                .subscribe(subject)

                            longClicks { true }
                                .subscribe {
                                    viewModel.setToolbarState(MultiSelectState)
                                    (holder.bindingAdapter as NoteListAdapter).transAllMultiSelectDefaultNote()
                                }
                        }
                        else -> {
                            clicks()
                                .subscribe {
                                    (holder.bindingAdapter as NoteListAdapter).setMultiSelectCheck(position, holder.binding)
                                }
                        }
                    }

                }
            }
            is SingleDeleteHolder -> {
                holder.binding.menuDeleteContainer.apply {
                    clicks()
                        .map { holder.binding.noteUiModel }
                        .subscribe( holder.binding.clickSubject  )
                }
            }
        }
    }

}