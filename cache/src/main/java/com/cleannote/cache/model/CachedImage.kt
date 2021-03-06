package com.cleannote.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.cleannote.cache.database.constants.Constants

@Entity(
    tableName = Constants.TABLE_IMAGE,
    foreignKeys = [
        ForeignKey(
            entity = CachedNote::class,
            parentColumns = ["id"],
            childColumns = ["note_pk"],
            onDelete = CASCADE
        )
    ]
)
data class CachedImage(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "image_pk")
    var imgPk: String,

    @ColumnInfo(name = "note_pk", index = true)
    var notePk: String,

    @ColumnInfo(name = "img_path")
    var imagePath: String
)
