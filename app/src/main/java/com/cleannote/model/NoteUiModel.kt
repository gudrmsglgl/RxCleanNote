package com.cleannote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class NoteUiModel(val id: String,
                  val title: String,
                  val body: String,
                  val updated_at: String,
                  val created_at: String,
                  var isShowMenu: Boolean = false): Parcelable{

    override fun toString(): String {
        return "Note id: $id, title: $title, body: $body, updated: $updated_at, created: $created_at"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass ) return false

        val otherUiModel = other as NoteUiModel

        if (id != otherUiModel.id) return false
        if (title != otherUiModel.title) return false
        if (body != otherUiModel.body) return false
        if (created_at != otherUiModel.created_at) return false
        if (isShowMenu != otherUiModel.isShowMenu) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + updated_at.hashCode()
        result = 31 * result + created_at.hashCode()
        result = 31 * result + isShowMenu.hashCode()
        return result
    }


}
