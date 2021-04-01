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
    @DisplayName("TestCase[Remote Complete, Cache SuccessRow]: Call RemoteDataStore Insert ⭕ CacheDataStore Insert ⭕")
    fun remoteCacheDataStoreSuccessThenCallRemoteCacheDataStore(){
        stubContainer {
            rDataStoreStubber
                .insertNote(param = noteEntity, stub = Completable.complete())
            cDataStoreStubber
                .insertNote(param = noteEntity, stub = successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        dataStoreVerifyScope {
            cacheInsertNote(noteEntity)
            times(1)
                .remoteInsertNote(noteEntity)
        }
    }

    @Test
    @DisplayName("TestCase[Remote Complete, Cache SuccessRow]: CacheStore InsertCacheNewNote -> RemoteStore InsertRemoteNewNote")
    fun verifyOrderingFirstCacheDataStoreNextRemoteDataStore(){
        stubContainer {
            rDataStoreStubber
                .insertNote(param = noteEntity, stub = Completable.complete())
            cDataStoreStubber
                .insertNote(param = noteEntity, stub = successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        dataStoreVerifyScope {
            inOrder(cDataStore, rDataStore){
                cacheInsertNote(noteEntity)
                times(1)
                    .remoteInsertNote(noteEntity)
            }
        }
    }

    @Test
    @DisplayName("TestCase[Remote Complete, Cache SuccessRow]: AssertComplete")
    fun cacheAndRemoteStoreSuccessThenAssertComplete(){
        stubContainer {
            rDataStoreStubber
                .insertNote(param = noteEntity, stub = Completable.complete())
            cDataStoreStubber
                .insertNote(param = noteEntity, stub = successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase[Remote Complete, Cache SuccessRow]: AssertValue -> successRow ")
    fun cacheAndRemoteStoreSuccessThenAssertValueSuccessRow(){
        stubContainer {
            rDataStoreStubber
                .insertNote(param = noteEntity, stub = Completable.complete())
            cDataStoreStubber
                .insertNote(param = noteEntity, stub = successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertValue(successRow)
    }

   @Test
   @DisplayName("TestCase[Cache Throwable]: Call CacheDataStore Insert ⭕ RemoteDataStore ❌")
   fun cacheDataStoreThrowThenOnlyCallCacheDataStore(){
       stubContainer {
           rDataStoreStubber
               .insertNote(param = noteEntity, stub = Completable.complete())
           cDataStoreStubber
               .insertThrowable(param = noteEntity, stub = RuntimeException())
       }

       whenDataRepositoryInsertNote(note)
           .test()

       dataStoreVerifyScope {
           cacheInsertNote(noteEntity)
           never()
               .remoteInsertNote(noteEntity)
       }
   }

   @Test
   @DisplayName("TestCase[Cache Throwable]: NoteDataRepo AssertComplete")
   fun cacheStoreThrowableThenAssertComplete(){
       stubContainer {
           rDataStoreStubber
               .insertNote(param = noteEntity, stub = Completable.complete())
           cDataStoreStubber
               .insertThrowable(param = noteEntity, stub = RuntimeException())
       }

       whenDataRepositoryInsertNote(note)
           .test()
           .assertComplete()
   }
   
   @Test
   @DisplayName("TestCase[Cache Throwable]: AssertValue -> FailRow[-1L]")
   fun cacheAndRemoteStoreSuccessThenAssertValueFailRow(){
       stubContainer {
           rDataStoreStubber
               .insertNote(param = noteEntity, stub = Completable.complete())
           cDataStoreStubber
               .insertThrowable(param = noteEntity, stub = RuntimeException())
       }

       whenDataRepositoryInsertNote(note)
           .test()
           .assertValue(-1L)
   }
   
    @Test
    @DisplayName("TestCase[Remote Throwable, Cache SuccessRow]: Call RemoteDataStore Insert ⭕ CacheDataStore Insert ⭕")
    fun cacheDataStoreSuccessRemoteStoreThrowThenCallRemoteCacheDataStore(){
        stubContainer {
            rDataStoreStubber
                .insertThrowable(noteEntity, RuntimeException())
            cDataStoreStubber
                .insertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        dataStoreVerifyScope {
            times(1)
                .remoteInsertNote(noteEntity)
            cacheInsertNote(noteEntity)
        }
    }
    
    @Test
    @DisplayName("TestCase[Remote Throwable, Cache SuccessRow]: CacheStore Insert -> RemoteStore Insert")
    fun verifyOrderingFirstCacheStoreNextRemoteStore(){
        stubContainer {
            rDataStoreStubber
                .insertThrowable(noteEntity, RuntimeException())
            cDataStoreStubber
                .insertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        dataStoreVerifyScope {
            inOrder(cDataStore, rDataStore){
                cacheInsertNote(noteEntity)
                times(1)
                    .remoteInsertNote(noteEntity)
            }
        }
    }
    
    @Test
    @DisplayName("TestCase[Remote Throwable, Cache SuccessRow]: AssertComplete")
    fun cacheStoreSuccessRemoteThrowableThenAssertComplete(){
        stubContainer {
            rDataStoreStubber.insertThrowable(noteEntity, RuntimeException())
            cDataStoreStubber.insertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertComplete()
    }
    
    @Test
    @DisplayName("TestCase[Remote Throwable, Cache SuccessRow]: AssertValue -> SuccessRow")
    fun cacheStoreSuccessRemoteThrowableThenAssertValueSuccessRow(){
        stubContainer {
            rDataStoreStubber
                .insertThrowable(noteEntity, RuntimeException())
            cDataStoreStubber
                .insertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertValue(successRow)
    }

    private fun whenDataRepositoryInsertNote(note: Note) = noteDataRepository.insertNewNote(note)
}