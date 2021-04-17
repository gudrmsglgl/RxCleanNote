package com.cleannote.notedetail.edit

import android.app.Activity
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.notedetail.edit.PickerType.Companion.CAMERA
import com.cleannote.notedetail.edit.PickerType.Companion.LINK
import com.cleannote.notedetail.edit.dialog.LinkImageDialog
import com.github.dhaval2404.imagepicker.ImagePicker

class ImageLoader(
    private val fragment: NoteDetailEditFragment,
    private val glideManager: RequestManager
) {

    fun onLoaded(@PickerType type: Int, resultOk: (String) -> Unit) {
        when (type) {
            LINK -> LinkImageDialog(
                fragment.requireActivity(),
                glideManager,
                fragment.viewLifecycleOwner
            ).onUploadImage { resultOk.invoke(it) }
            else -> loadImagePicker(type, resultOk)
        }
    }

    private inline fun loadImagePicker(@PickerType type: Int, crossinline resultOk: (String) -> Unit) {
        val builder = ImagePicker.with(fragment)
        if (type == CAMERA)
            builder.cameraOnly()
        else
            builder.galleryOnly()
        builder.compress(1024)
            .start { resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        // You can also get File Path from intent
                        val filePath: String = ImagePicker.getFilePath(data)!!
                        resultOk.invoke(filePath)
                    }
                    ImagePicker.RESULT_ERROR -> {
                        fragment.showToast(ImagePicker.getError(data))
                    }
                    else -> {
                        fragment.showToast(fragment.getString(R.string.cancel_message))
                    }
                }
            }
    }
}
