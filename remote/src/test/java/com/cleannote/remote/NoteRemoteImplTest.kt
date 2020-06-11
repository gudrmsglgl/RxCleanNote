package com.cleannote.remote

import com.cleannote.data.model.NoteEntity
import com.cleannote.remote.mapper.NoteEntityMapper
import com.cleannote.remote.mapper.UserEntityMapper
import com.cleannote.remote.model.NoteModel
import com.cleannote.remote.test.factory.NoteFactory
import com.cleannote.remote.test.factory.UserFactory
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteRemoteImplTest {
    private lateinit var entityMapper: NoteEntityMapper
    private lateinit var noteService: NoteService

    private lateinit var noteRemoteImpl: NoteRemoteImpl

    private val count = 2
    private val noteEntity = NoteFactory.createNoteEntity(
        "7","title#7","body#7")
    private lateinit var noteModels : List<NoteModel>
    private lateinit var noteEntities: List<NoteEntity>

    private lateinit var userEntityMapper: UserEntityMapper
    private val userModels = UserFactory.makeUserModels()

    @BeforeEach
    fun setUp(){
        noteModels = NoteFactory.createNoteMoelList(count)
        noteEntities = NoteFactory.createNoteEntityList(count)
        entityMapper = mock()
        userEntityMapper = mock()
        noteService = mock{
            on { insertNote(noteEntity.id, noteEntity.title,
                noteEntity.body, noteEntity.updated_at, noteEntity.created_at)
            } doReturn Completable.complete()
            on { getNotes(1, count) } doReturn Flowable.just(noteModels)
            on { login(UserFactory.USER_ID) } doReturn Flowable.just(userModels)
        }
        noteRemoteImpl = NoteRemoteImpl(noteService, entityMapper, userEntityMapper)
    }

    @Test
    fun getNumNotesComplete(){
        noteModels.forEachIndexed { index, noteModel ->
            whenever(entityMapper.mapFromRemote(noteModel)).thenReturn(noteEntities[index])
        }
        val testObserver = noteRemoteImpl.getNumNotes().test()
        testObserver.assertComplete()
    }

    @Test
    fun getNumNotesReturnData(){
        noteModels.forEachIndexed { index, noteModel ->
            whenever(entityMapper.mapFromRemote(noteModel)).thenReturn(noteEntities[index])
        }
        val testObserver = noteRemoteImpl.getNumNotes().test()
        println(testObserver.values())
        testObserver.assertValue(noteEntities)
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
        val userEntities = UserFactory.makeUserEntities()
        userEntities.forEachIndexed { index, userEntity ->
            whenever(userEntityMapper.mapFromRemote(userModels[index])).thenReturn(userEntity)
        }
        val testObserver = noteRemoteImpl.login(UserFactory.USER_ID).test()
        testObserver.assertValue(userEntities)
    }
}