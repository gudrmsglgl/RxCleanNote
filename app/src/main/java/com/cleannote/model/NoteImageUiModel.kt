package com.cleannote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteImageUiModel (val img_pk: String, val note_pk: String, val img_path: String):Parcelable