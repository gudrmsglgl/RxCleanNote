package com.cleannote.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cleannote.cache.database.constants.Constants

@Entity(tableName = Constants.TABLE_NOTE)
data class CachedNote(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "body")
    var body: String,

    @ColumnInfo(name = "updated_at")
    var updated_at: String,

    @ColumnInfo(name = "created_at")
    var created_at: String
)
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CachedNote

        if (id != other.id) return false
        if (title != other.title) return false
        if (body != other.body) return false
        if (created_at != other.created_at) return false

        return true
    }
}