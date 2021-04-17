package com.cleannote.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cleannote.app.BuildConfig
import com.cleannote.app.R
import com.cleannote.extension.hideKeyboard
import com.cleannote.model.NoteImageUiModel
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.Keys.GLIDE_DETAIL_VIEW_STATE_KEY
import com.cleannote.notedetail.edit.EditImagesAdapter
import com.cleannote.notedetail.view.GlideLoadState.Companion.STATE_FAIL
import com.cleannote.notedetail.view.GlideLoadState.Companion.STATE_SUCCESS
import com.cleannote.notedetail.view.ImageViewAdapter
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
    ) {
        val image: Any =
            if (noteUiModel.images.isNullOrEmpty()) R.drawable.empty_holder
            else noteUiModel.images.get(0).imgPath
        glideRequestManager
            .applyDefaultRequestOptions(
                RequestOptions.bitmapTransform(RoundedCorners(10))
            )
            .load(image)
            .thumbnail(0.1f)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["imageViews"])
    fun submitViewPagerAdapterOnDetailView(
        pager: ViewPager2,
        images: List<NoteImageUiModel>?
    ) {
        (pager.adapter as ImageViewAdapter).submitList(images)
    }

    @JvmStatic
    @BindingAdapter(value = ["attachImages"])
    fun submitRcvAdapterOnDetailEditView(
        recyclerView: RecyclerView,
        images: List<NoteImageUiModel>?
    ) {
        (recyclerView.adapter as EditImagesAdapter).submitList(images)
        val scroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun getHorizontalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_END
            }
        }
        scroller.targetPosition = 0
        recyclerView.layoutManager?.startSmoothScroll(scroller)
    }

    @JvmStatic
    @BindingAdapter(value = ["dvGlideManager", "dvImageModel"])
    fun loadImgDetailViewHolder(
        imageView: ImageView,
        glideManager: RequestManager,
        item: NoteImageUiModel
    ) {
        if (BuildConfig.DEBUG) {
            glideManager
                .load(item.imgPath)
                .thumbnail(0.1f)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        imageView.setTag(GLIDE_DETAIL_VIEW_STATE_KEY, STATE_SUCCESS)
                        imageView.setImageDrawable(resource)
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        imageView.setImageDrawable(placeholder)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        imageView.setTag(GLIDE_DETAIL_VIEW_STATE_KEY, STATE_FAIL)
                        imageView.setImageDrawable(errorDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        imageView.setImageDrawable(placeholder)
                    }
                })
        } else {
            glideManager
                .load(item.imgPath)
                .thumbnail(0.1f)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["glideManager", "imageModel"])
    fun loadEditImage(
        imageView: ImageView,
        glideManager: RequestManager,
        imageModel: NoteImageUiModel
    ) {
        glideManager
            .applyDefaultRequestOptions(
                RequestOptions.bitmapTransform(RoundedCorners(30))
            )
            .load(imageModel.imgPath)
            .thumbnail(0.1f)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter(value = ["releaseFocus"])
    fun releaseFocus(
        view: View,
        textMode: TextMode
    ) {
        if (textMode != TextMode.EditMode) {
            view.takeIf { it.isFocused }?.clearFocus()
            view.hideKeyboard()
        }
    }
}
