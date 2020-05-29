package com.cleannote.data

import com.cleannote.data.mapper.NoteMapper
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.source.NoteCacheDataStore
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.data.source.NoteRemoteDataStore
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.domain.model.Note
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteDataRepositoryTest {

    private lateinit var noteDataRepository: NoteDataRepository

    private lateinit var noteDataStoreFactory: NoteDataStoreFactory
    private lateinit var noteMapper: NoteMapper

    private lateinit var noteCacheDataStore: NoteCacheDataStore
    private lateinit var noteRemoteDataStore: NoteRemoteDataStore
    private lateinit var noteEntities: List<NoteEntity>
    private val noteEntity = NoteFactory.createNoteEntity("#1","title#1","body#1")
    private val noteEntity2 = NoteFactory.createNoteEntity("#2","title#2","body#2")
    private val note: Note = NoteFactory.createNote("#1","title#1","body#1")
    private val note2: Note = NoteFactory.createNote("#2", "title#2", "body#2")
    private val insertedSuccess = 1L
    private val insertedFail = -1L

    @BeforeEach
    fun setUp(){

        noteMapper = mock(){
            on { mapToEntity(note) } doReturn noteEntity
            on { mapToEntity(note2) } doReturn noteEntity2
        }
        noteEntities = NoteFactory.createNoteEntityList(10)
        noteCacheDataStore = mock {
            on { insertCacheNewNote(noteEntity) } doReturn Single.just(insertedSuccess)
            on { insertCacheNewNote(noteEntity2) } doReturn Single.just(insertedFail)
        }
        noteRemoteDataStore = mock{
            on { getNumNotes() }.doReturn(Flowable.just(noteEntities))
            on {
                insertRemoteNewNote(noteEntity)
                insertRemoteNewNote(noteEntity2)
            } doReturn Completable.complete()
        }
        noteDataStoreFactory = mock{
            on { retrieveRemoteDataStore() }.doReturn(noteRemoteDataStore)
            on { retrieveCacheDataStore() } doReturn noteCacheDataStore
        }
        noteDataRepository = NoteDataRepository(noteDataStoreFactory, noteMapper)
    }

    @Test
    fun getNumNoteComplete(){
        val notes: List<Note> = NoteFactory.createNoteList(10)
        // note 를 기준으로 잡고 assert 로 확인 가능 맵핑 됬는 지
        notes.forEachIndexed { index, note ->
            whenever(noteMapper.mapFromEntity(noteEntities[index])).thenReturn(note)
        }
        val testObserver = noteDataRepository.getNumNotes().test()
        testObserver.assertComplete()
        testObserver.assertValue(notes)
    }

    @Test
    fun insertNewNoteComplete(){
        val testObserver = noteDataRepository.insertNewNote(note).test()
        testObserver.assertComplete()
    }

    @Test
    fun insertNewNoteReturnSuccess(){
        val testObserver = noteDataRepository.insertNewNote(note).test()
        testObserver.assertValue(insertedSuccess)
    }

    @Test
    fun insertNewNoteReturnFail(){
        val testObserver = noteDataRepository.insertNewNote(note2).test()
        testObserver.assertValue(insertedFail)
    }
}