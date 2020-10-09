package com.cleannote.remote

import com.cleannote.remote.common.BaseRemote
import com.cleannote.remote.extensions.transNoteEntities
import com.cleannote.remote.extensions.transUserEntities
import com.cleannote.remote.model.UserModel
import com.cleannote.remote.test.factory.NoteFactory
import com.cleannote.remote.test.factory.QueryFactory
import com.cleannote.remote.test.factory.UserFactory
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.RuntimeException

class NoteRemoteImplTest: BaseRemote() {

    private lateinit var noteRemoteImpl: NoteRemoteImpl

    private val noteEntity = NoteFactory.createNoteEntity(
        "7","title#7","body#7")


    private val defaultQuery = QueryFactory.makeQueryEntity()

    @BeforeEach
    fun setUp(){
        noteService = mock{
            on { insertNote(noteEntity.id, noteEntity.title,
                noteEntity.body, noteEntity.updated_at, noteEntity.created_at)
            } doReturn Completable.complete()
        }
        noteRemoteImpl = NoteRemoteImpl(noteService)
    }


    @Test
    fun insertNewNoteComplete(){
        val testObserver = noteRemoteImpl.insertRemoteNewNote(noteEntity).test()
        testObserver.assertComplete()
    }

    @Test
    fun loginComplete(){
        val testObserver = noteRemoteImpl.login(UserFactory.USER_ID).test()
        testObserver.assertComplete()
    }

    @Test
    fun loginReturnData(){
        val stubUserId = UserFactory.USER_ID
        val userModels = UserFactory.makeUserModels(2)
        noteService stubLogin (stubUserId to userModels)

        noteRemoteImpl.login(stubUserId)
            .test()
            .assertComplete()
    }

    @Test
    fun searchNotesComplete(){
        val noteModels = NoteFactory.createNoteMoelList(defaultQuery.limit)

        noteService stubSearchNotes (defaultQuery to noteModels)

        val testObserver = noteRemoteImpl.searchNotes(defaultQuery).test()
        testObserver.assertComplete()
        testObserver.assertValue(noteModels.transNoteEntities())
    }

    @Test
    fun searchNotesThrowErrorThenEmpty() {
        noteService stubSearchNotesThrow (defaultQuery to RuntimeException())

        noteRemoteImpl.searchNotes(defaultQuery)
            .test()
            .assertValue(emptyList())

    }

    @Test
    fun updateNoteThrowUnsupportedOperationException(){
        val updateNoteEntity = NoteFactory.createNoteEntity(title = "test")
        Assertions.assertThrows(UnsupportedOperationException::class.java){
            noteRemoteImpl.updateNote(updateNoteEntity)
        }
    }
}