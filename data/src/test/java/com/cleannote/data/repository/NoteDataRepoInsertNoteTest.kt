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
    @DisplayName("TestCase[Remote⭕, Cache⭕]: Remote Cache DataStore 모두 호출")
    fun remoteCacheDataStoreSuccessThenCallRemoteCacheDataStore(){
        stubContainer {
            remoteDataStore {
                stubInsertNote(param = noteEntity, stub = Completable.complete())
            }
            cacheDataStore {
                stubInsertNote(param = noteEntity, stub = successRow)
            }
        }

        whenDataRepositoryInsertNote(note)
            .test()

        verifyContainer{

            verify(cacheDataStore)
                .insertCacheNewNote(noteEntity)

            verify(remoteDataStore)
                .insertRemoteNewNote(noteEntity)

        }
    }

    @Test
    @DisplayName("TestCase[Remote⭕, Cache⭕]: CacheStore 불러온 후 RemoteStore 호출")
    fun verifyOrderingFirstCacheDataStoreNextRemoteDataStore(){
        stubContainer {
            remoteDataStore {
                stubInsertNote(param = noteEntity, stub = Completable.complete())
            }
            cacheDataStore {
                stubInsertNote(param = noteEntity, stub = successRow)
            }
        }

        whenDataRepositoryInsertNote(note)
            .test()

        verifyContainer{
            inOrder(cacheDataStore, remoteDataStore){

                verify(cacheDataStore)
                    .insertCacheNewNote(noteEntity)

                verify(remoteDataStore)
                    .insertRemoteNewNote(noteEntity)

            }
        }
    }


    @Test
    @DisplayName("TestCase[Remote⭕, Cache⭕]: NoteDataRepo AssertComplete")
    fun cacheAndRemoteStoreSuccessThenAssertComplete(){
        stubContainer {
            remoteDataStore.stubInsertNote(param = noteEntity, stub = Completable.complete())
            cacheDataStore.stubInsertNote(param = noteEntity, stub = successRow)
        }
        whenDataRepositoryInsertNote(note)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase[Remote⭕, Cache⭕]: NoteDataRepo AssertValue -> successRow ")
    fun cacheAndRemoteStoreSuccessThenAssertValueSuccessRow(){
        stubContainer {
            remoteDataStore.stubInsertNote(param = noteEntity, stub = Completable.complete())
            cacheDataStore.stubInsertNote(param = noteEntity, stub = successRow)
        }
        whenDataRepositoryInsertNote(note)
            .test()
            .assertValue(successRow)
    }

   @Test
   @DisplayName("TestCase[cache\uD83D\uDC1C]: Cache DataStore 호출 Remote DataStore 호출X")
   fun cacheDataStoreThrowThenOnlyCallCacheDataStore(){
       stubContainer {
           remoteDataStore {
               stubInsertNote(param = noteEntity, stub = Completable.complete())
           }
           cacheDataStore {
               stubInsertThrowable(param = noteEntity, stub = RuntimeException())
           }
       }

       whenDataRepositoryInsertNote(note)
           .test()

       verifyContainer{

           verify(cacheDataStore)
               .insertCacheNewNote(noteEntity)

           verify(remoteDataStore, never())
               .insertRemoteNewNote(noteEntity)

       }
   }

   @Test
   @DisplayName("TestCase[cache\uD83D\uDC1C]: NoteDataRepo AssertComplete")
   fun cacheStoreThrowableThenAssertComplete(){
       stubContainer {
           remoteDataStore {
               stubInsertNote(param = noteEntity, stub = Completable.complete())
           }
           cacheDataStore {
               stubInsertThrowable(param = noteEntity, stub = RuntimeException())
           }
       }
       whenDataRepositoryInsertNote(note)
           .test()
           .assertComplete()
   }
   
   @Test
   @DisplayName("TestCase[cache\uD83D\uDC1C]: NoteDataRepo AssertValue -> FailRow[-1L]")
   fun cacheAndRemoteStoreSuccessThenAssertValueFailRow(){
       stubContainer {
           remoteDataStore {
               stubInsertNote(param = noteEntity, stub = Completable.complete())
           }
           cacheDataStore {
               stubInsertThrowable(param = noteEntity, stub = RuntimeException())
           }
       }
       whenDataRepositoryInsertNote(note)
           .test()
           .assertValue(-1L)
   }
   
    @Test
    @DisplayName("TestCase[Remote\uD83D\uDC1C, cache⭕]: Remote Cache DataStore 호출")
    fun cacheDataStoreSuccessRemoteStoreThrowThenCallRemoteCacheDataStore(){
        stubContainer {
            remoteDataStore.stubInsertThrowable(noteEntity, RuntimeException())
            cacheDataStore.stubInsertNote(noteEntity, successRow)
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
    @DisplayName("TestCase[Remote\uD83D\uDC1C, cache⭕]: CacheStore 불러온 후 RemoteStore 호출")
    fun verifyOrderingFirstCacheStoreNextRemoteStore(){
        stubContainer {
            remoteDataStore.stubInsertThrowable(noteEntity, RuntimeException())
            cacheDataStore.stubInsertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()

        verifyContainer{
            inOrder(cacheDataStore, remoteDataStore){

                verify(cacheDataStore)
                    .insertCacheNewNote(noteEntity)

                verify(remoteDataStore)
                    .insertRemoteNewNote(noteEntity)

            }
        }
    }
    
    @Test
    @DisplayName("TestCase[Remote\uD83D\uDC1C, cache⭕]: NoteDataRepo AssertComplete")
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
    @DisplayName("TestCase[Remote\uD83D\uDC1C, cache⭕]: NoteDataRepo AssertValue -> SuccessRow")
    fun cacheStoreSuccessRemoteSThrowableThenAssertValueSuccessRow(){
        stubContainer {
            remoteDataStore.stubInsertThrowable(noteEntity, RuntimeException())
            cacheDataStore.stubInsertNote(noteEntity, successRow)
        }

        whenDataRepositoryInsertNote(note)
            .test()
            .assertValue(successRow)
    }

}