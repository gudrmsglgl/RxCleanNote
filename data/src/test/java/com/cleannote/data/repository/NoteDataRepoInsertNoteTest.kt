package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.*
import com.cleannote.data.test.factory.NoteFactory
import com.cleannote.domain.model.Note
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Completable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class NoteDataRepoInsertNoteTest: BaseNoteRepositoryTest() {

    private val note: Note = NoteFactory.createNote(title = "title#1")
    private val noteEntity = note.transNoteEntity()
    private val successRow = 1L

    @Test
    @DisplayName("TestCase[Remote Complete, Cache SuccessRow]: RemoteDataStore Insert ⭕ CacheDataStore Insert ⭕")
    fun remoteCacheDataStoreSuccessThenCallRemoteCacheDataStore(){
        stubContainer {
            remoteDataStore
                .stubInsertNote(param = noteEntity, stub = Completable.complete())
            cacheDataStore
                .stubInsertNote(param = noteEntity, stub = successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        verifyContainer {
            verify(cacheDataStore)
                .insertCacheNewNote(noteEntity)
            verify(remoteDataStore)
                .insertRemoteNewNote(noteEntity)
        }
    }

    @Test
    @DisplayName("TestCase[Remote Complete, Cache SuccessRow]: CacheStore InsertCacheNewNote -> RemoteStore InsertRemoteNewNote")
    fun verifyOrderingFirstCacheDataStoreNextRemoteDataStore(){
        stubContainer {
            remoteDataStore
                .stubInsertNote(param = noteEntity, stub = Completable.complete())
            cacheDataStore
                .stubInsertNote(param = noteEntity, stub = successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        verifyContainer {
            inOrder(cacheDataStore, remoteDataStore){
                verify(cacheDataStore)
                    .insertCacheNewNote(noteEntity)
                verify(remoteDataStore)
                    .insertRemoteNewNote(noteEntity)
            }
        }
    }

    @Test
    @DisplayName("TestCase[Remote Complete, Cache SuccessRow]: AssertComplete")
    fun cacheAndRemoteStoreSuccessThenAssertComplete(){
        stubContainer {
            remoteDataStore
                .stubInsertNote(param = noteEntity, stub = Completable.complete())
            cacheDataStore
                .stubInsertNote(param = noteEntity, stub = successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase[Remote Complete, Cache SuccessRow]: AssertValue -> successRow ")
    fun cacheAndRemoteStoreSuccessThenAssertValueSuccessRow(){
        stubContainer {
            remoteDataStore
                .stubInsertNote(param = noteEntity, stub = Completable.complete())
            cacheDataStore
                .stubInsertNote(param = noteEntity, stub = successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertValue(successRow)
    }

   @Test
   @DisplayName("TestCase[Cache Throwable]: CacheDataStore Insert ⭕ RemoteDataStore ❌")
   fun cacheDataStoreThrowThenOnlyCallCacheDataStore(){
       stubContainer {
           remoteDataStore
               .stubInsertNote(param = noteEntity, stub = Completable.complete())
           cacheDataStore
               .stubInsertThrowable(param = noteEntity, stub = RuntimeException())
       }

       whenDataRepositoryInsertNote(note)
           .test()

       verifyContainer {
           verify(cacheDataStore)
               .insertCacheNewNote(noteEntity)
           verify(remoteDataStore, never())
               .insertRemoteNewNote(noteEntity)
       }
   }

   @Test
   @DisplayName("TestCase[Cache Throwable]: NoteDataRepo AssertComplete")
   fun cacheStoreThrowableThenAssertComplete(){
       stubContainer {
           remoteDataStore
               .stubInsertNote(param = noteEntity, stub = Completable.complete())
           cacheDataStore
               .stubInsertThrowable(param = noteEntity, stub = RuntimeException())
       }

       whenDataRepositoryInsertNote(note)
           .test()
           .assertComplete()
   }
   
   @Test
   @DisplayName("TestCase[Cache Throwable]: AssertValue -> FailRow[-1L]")
   fun cacheAndRemoteStoreSuccessThenAssertValueFailRow(){
       stubContainer {
           remoteDataStore
               .stubInsertNote(param = noteEntity, stub = Completable.complete())
           cacheDataStore
               .stubInsertThrowable(param = noteEntity, stub = RuntimeException())
       }

       whenDataRepositoryInsertNote(note)
           .test()
           .assertValue(-1L)
   }
   
    @Test
    @DisplayName("TestCase[Remote Throwable, Cache SuccessRow]: RemoteDataStore Insert ⭕ CacheDataStore Insert ⭕")
    fun cacheDataStoreSuccessRemoteStoreThrowThenCallRemoteCacheDataStore(){
        stubContainer {
            remoteDataStore
                .stubInsertThrowable(noteEntity, RuntimeException())
            cacheDataStore
                .stubInsertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        verifyContainer {
            verify(remoteDataStore)
                .insertRemoteNewNote(noteEntity)
            verify(cacheDataStore)
                .insertCacheNewNote(noteEntity)
        }
    }
    
    @Test
    @DisplayName("TestCase[Remote Throwable, Cache SuccessRow]: CacheStore Insert -> RemoteStore Insert")
    fun verifyOrderingFirstCacheStoreNextRemoteStore(){
        stubContainer {
            remoteDataStore
                .stubInsertThrowable(noteEntity, RuntimeException())
            cacheDataStore
                .stubInsertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        verifyContainer {
            inOrder(cacheDataStore, remoteDataStore){
                verify(cacheDataStore)
                    .insertCacheNewNote(noteEntity)
                verify(remoteDataStore)
                    .insertRemoteNewNote(noteEntity)
            }
        }
    }
    
    @Test
    @DisplayName("TestCase[Remote Throwable, Cache SuccessRow]: AssertComplete")
    fun cacheStoreSuccessRemoteThrowableThenAssertComplete(){
        stubContainer {
            remoteDataStore.stubInsertThrowable(noteEntity, RuntimeException())
            cacheDataStore.stubInsertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertComplete()
    }
    
    @Test
    @DisplayName("TestCase[Remote Throwable, Cache SuccessRow]: AssertValue -> SuccessRow")
    fun cacheStoreSuccessRemoteThrowableThenAssertValueSuccessRow(){
        stubContainer {
            remoteDataStore
                .stubInsertThrowable(noteEntity, RuntimeException())
            cacheDataStore
                .stubInsertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertValue(successRow)
    }

}