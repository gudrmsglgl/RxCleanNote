package com.cleannote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteUiModel(val id: String,
                       var title: String,
                       var body: String,
                       var updated_at: String,
                       val created_at: String,
                       var mode: NoteMode = NoteMode.Default): Parcelable

enum class NoteMode{
    Default,
    SingleDelete,
    MultiDefault,
    MultiSelected
}