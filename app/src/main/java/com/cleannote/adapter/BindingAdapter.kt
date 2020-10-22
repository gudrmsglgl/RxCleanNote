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
import com.cleannote.model.NoteUiModel


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

}