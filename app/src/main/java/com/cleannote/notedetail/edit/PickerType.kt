package com.cleannote.notedetail.edit

import androidx.annotation.IntDef
import com.cleannote.notedetail.edit.PickerType.Companion.CAMERA
import com.cleannote.notedetail.edit.PickerType.Companion.GALLERY
import com.cleannote.notedetail.edit.PickerType.Companion.LINK

@IntDef(CAMERA, GALLERY, LINK)
@Retention(AnnotationRetention.SOURCE)
annotation class PickerType {
    companion object {
        const val CAMERA = 1
        const val GALLERY = 2
        const val LINK = 3
    }
}
