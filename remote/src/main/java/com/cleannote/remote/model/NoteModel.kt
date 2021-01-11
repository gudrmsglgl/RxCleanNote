package com.cleannote.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NoteModel(
    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("title")
    @Expose
    val title: String,

    @SerializedName("body")
    @Expose
    val body: String,

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String,

    @SerializedName("created_at")
    @Expose
    val createdAt: String,

    @SerializedName("img_paths")
    @Expose
    val images: List<String>?
)