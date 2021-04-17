package com.cleannote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteImageUiModel(val imgPk: String, val notePk: String, val imgPath: String) : Parcelable
