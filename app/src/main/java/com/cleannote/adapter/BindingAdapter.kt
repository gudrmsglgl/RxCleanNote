package com.cleannote.adapter

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.cleannote.app.R
import com.cleannote.extension.hideKeyboard
import com.cleannote.model.NoteImageUiModel
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.AttachImagesAdapter
import com.cleannote.presentation.data.notedetail.TextMode


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
    @BindingAdapter(value = ["attachImages"])
    fun attachDataToAdapter(
        recyclerView: RecyclerView,
        images: List<NoteImageUiModel>?
    ){
        (recyclerView.adapter as AttachImagesAdapter).submitList(images)
        val scroller = object : LinearSmoothScroller(recyclerView.context){
            override fun getHorizontalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }
        scroller.targetPosition = 0
        recyclerView.layoutManager?.startSmoothScroll(scroller)
    }

    @JvmStatic
    @BindingAdapter(value = ["glideManager", "imageModel"])
    fun loadDetailImage(
        imageView: ImageView,
        glideManager: RequestManager,
        imageModel: NoteImageUiModel
    ){
        glideManager
            .applyDefaultRequestOptions(
                RequestOptions
                    .bitmapTransform(RoundedCorners(10))
            )
            .load(imageModel.img_path)
            .thumbnail(0.1f)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["releaseFocus"])
    fun releaseFocus(
        view: View,
        textMode: TextMode
    ){
        if (textMode != TextMode.EditMode){
            view.takeIf { it.isFocused }?.clearFocus()
            view.hideKeyboard()
        }
    }

}