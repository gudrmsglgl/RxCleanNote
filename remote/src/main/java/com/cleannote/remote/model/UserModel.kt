package com.cleannote.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("userId")
    @Expose
    val userId: String,

    @SerializedName("nick")
    @Expose
    val nick: String
)
