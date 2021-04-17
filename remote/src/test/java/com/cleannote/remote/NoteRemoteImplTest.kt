package com.cleannote.remote

import com.cleannote.remote.common.BaseRemote
import com.cleannote.remote.extensions.transNoteEntities
import com.cleannote.remote.test.factory.NoteFactory
import com.cleannote.remote.test.factory.QueryFactory
import com.cleannote.remote.test.factory.UserFactory
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Completable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.RuntimeException

class NoteRemoteImplTest : BaseRemote() {

    private lateinit var noteRemoteImpl: NoteRemoteImpl

    private val noteEntity = NoteFactory.createNoteEntity(
        "7", "title#7", "body#7"
    )

    @BeforeEach
    fun setUp() {
        noteService = mock {
            on {
                insertNote(
                    noteEntity.id, noteEntity.title,
                    noteEntity.body, noteEntity.updatedAt, noteEntity.createdAt
                )
            } doReturn Completable.complete()
        }
        noteRemoteImpl = NoteRemoteImpl(noteService)
    }

    @Test
    fun insertNewNoteComplete() {
        val testObserver = noteRemoteImpl.insertRemoteNewNote(noteEntity).test()
        testObserver.assertComplete()
    }

    @Test
    fun loginComplete() {
        noteService stubLogin (UserFactory.USER_ID to UserFactory.makeUserModels(2))
        val testObserver = noteRemoteImpl.login(UserFactory.USER_ID).test()
        testObserver.assertComplete()
    }

    @Test
    fun loginReturnData() {
        val stubUserId = UserFactory.USER_ID
        val userModels = UserFactory.makeUserModels(2)
        noteService stubLogin (stubUserId to userModels)

        noteRemoteImpl.login(stubUserId)
            .test()
            .assertComplete()
    }

    @Test
    fun searchNotesComplete() {
        val defaultQuery = QueryFactory.makeQueryEntity()
        val noteModels = NoteFactory.createNoteModelList(defaultQuery.limit)

        noteService stubSearchNotes (defaultQuery to noteModels)

        noteRemoteImpl.searchNotes(defaultQuery)
            .test()
            .assertComplete()
    }

    @Test
    fun searchNotesNotEmptyThenReturnRemoteNotes() {
        val defaultQuery = QueryFactory.makeQueryEntity()
        val noteModels = NoteFactory.createNoteModelList(defaultQuery.limit)

        noteService stubSearchNotes (defaultQuery to noteModels)

        noteRemoteImpl.searchNotes(defaultQuery)
            .test()
            .assertValue(noteModels.transNoteEntities())
    }

    @Test
    fun searchNotesThrowErrorThenEmpty() {
        val defaultQuery = QueryFactory.makeQueryEntity()
        noteService stubSearchNotesThrow (defaultQuery to RuntimeException())

        noteRemoteImpl.searchNotes(defaultQuery)
            .test()
            .assertValue(emptyList())
    }

    @Test
    fun updateNoteThrowUnsupportedOperationException() {
        val updateNoteEntity = NoteFactory.createNoteEntity(title = "test")
        Assertions.assertThrows(UnsupportedOperationException::class.java) {
            noteRemoteImpl.updateNote(updateNoteEntity)
        }
    }
}
