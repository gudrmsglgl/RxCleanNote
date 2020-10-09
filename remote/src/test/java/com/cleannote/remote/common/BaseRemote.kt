package com.cleannote.remote.common

import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.remote.NoteService
import com.cleannote.remote.mapper.NoteEntityMapper
import com.cleannote.remote.model.NoteModel
import com.cleannote.remote.model.UserModel
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable

abstract class BaseRemote {
    lateinit var noteService: NoteService

    infix fun NoteService.stubLogin(stub: Pair<String, List<UserModel>>){
        whenever(this.login(stub.first)).thenReturn(Flowable.just(stub.second))
    }

    infix fun NoteService.stubSearchNotes(stub: Pair<QueryEntity, List<NoteModel>>){
        whenever(this.searchNotes(
            stub.first.page, stub.first.limit, stub.first.sort,
            stub.first.order, stub.first.like, stub.first.like
        )).thenReturn(Flowable.just(stub.second))
    }

    infix fun NoteService.stubSearchNotesThrow(stub: Pair<QueryEntity, Throwable>){
        whenever(this.searchNotes(
            stub.first.page, stub.first.limit, stub.first.sort,
            stub.first.order, stub.first.like, stub.first.like
        )).thenReturn(Flowable.error(stub.second))
    }
}