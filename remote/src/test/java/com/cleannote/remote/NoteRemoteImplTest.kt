package com.cleannote.remote

import com.cleannote.data.model.NoteEntity
import com.cleannote.remote.mapper.NoteEntityMapper
import com.cleannote.remote.mapper.UserEntityMapper
import com.cleannote.remote.model.NoteModel
import com.cleannote.remote.test.factory.NoteFactory
import com.cleannote.remote.test.factory.QueryFactory
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

    private val defaultQuery = QueryFactory.makeQueryEntity()

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
            on { login(UserFactory.USER_ID) } doReturn Flowable.just(userModels)
        }
        noteRemoteImpl = NoteRemoteImpl(noteService, entityMapper, userEntityMapper)
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

    @Test
    fun searchNotesComplete(){
        val noteModels = NoteFactory.createNoteMoelList(defaultQuery.limit)
        val noteEntities = NoteFactory.createNoteEntityList(defaultQuery.limit)

        whenever(noteService.searchNotes(
            defaultQuery.page, defaultQuery.limit, defaultQuery.sort,
            defaultQuery.order, defaultQuery.like, defaultQuery.like
        )).thenReturn(Flowable.just(noteModels))

        noteEntities.forEachIndexed { index, noteEntity ->
            whenever(entityMapper.mapFromRemote(noteModels[index])).thenReturn(noteEntity)
        }

        val testObserver = noteRemoteImpl.searchNotes(defaultQuery).test()
        testObserver.assertComplete()
        testObserver.assertValue(noteEntities)
    }
}