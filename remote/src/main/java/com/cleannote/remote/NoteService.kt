package com.cleannote.remote

import com.cleannote.remote.model.NoteModel
import com.cleannote.remote.model.UserModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.reactivex.Completable
import io.reactivex.Flowable
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NoteService {

    @GET("users")
    fun login(
        @Query("userId") userId: String
    ): Flowable<List<UserModel>>

    @GET("memos")
    fun getNotes(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int = 2
    ): Flowable<List<NoteModel>>

    @POST
    fun insertNote(
        @Field("id") id: String,
        @Field("title") title: String,
        @Field("body") body: String,
        @Field("updated_at") updated_at: String,
        @Field("created_at") created_at: String
    ): Completable

    @GET("memos")
    fun searchNotes(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int,
        @Query("_sort") sort: String,
        @Query("_order") order: String,
        @Query("title_like") titleLike: String,
        @Query("body_like") bodyLike: String
    ): Flowable<List<NoteModel>>
}