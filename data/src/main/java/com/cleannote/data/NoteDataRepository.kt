package com.cleannote.data

import com.cleannote.data.mapper.NoteMapper
import com.cleannote.data.mapper.QueryMapper
import com.cleannote.data.mapper.UserMapper
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.QueryEntity
import com.cleannote.data.source.NoteDataStoreFactory
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.domain.model.User
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.rxkotlin.zipWith
import java.lang.Exception
import javax.inject.Inject

class NoteDataRepository
@Inject
constructor(
    private val factory: NoteDataStoreFactory,
    private val noteMapper: NoteMapper,
    private val userMapper: UserMapper,
    private val queryMapper: QueryMapper
): NoteRepository{

    override fun getNumNotes(): Flowable<List<Note>> = factory.retrieveRemoteDataStore()
        .getNumNotes()
        .map { listNoteEntity ->
            listNoteEntity.map {
                noteMapper.mapFromEntity(it)
            }
        }

    override fun insertNewNote(note: Note): Single<Long> = factory.retrieveCacheDataStore()
        .insertCacheNewNote(noteMapper.mapToEntity(note))
        .map {
            if (it > 0) {
                factory.retrieveRemoteDataStore().insertRemoteNewNote(noteMapper.mapToEntity(note))
                it
            } else {
                throw Exception()
            }
        }
        .onErrorReturn { -1L }

    override fun login(userId: String): Flowable<List<User>> = factory.retrieveRemoteDataStore()
        .login(userId)
        .map { users ->
            users.map {
                userMapper.mapFromEntity(it)
            }
        }

    override fun searchNotes(query: Query): Flowable<List<Note>> {
        val queryEntity = queryMapper.mapToEntity(query)
        return factory.retrieveCacheDataStore().isCached(queryEntity.page)
            .flatMapPublisher {
                factory.retrieveDataStore(it).searchNotes(queryEntity)
                    .zipWith(Flowable.just(it))
            }
            .flatMapSingle {
                val isCached: Boolean = it.second
                val loadedNoteEntities: List<NoteEntity> = it.first
                if (!isCached && loadedNoteEntities.isNotEmpty())
                    saveNotes(loadedNoteEntities, queryEntity.page).toSingle { it }
                else Single.just(it)
            }
            .flatMap {
                val isCached: Boolean = it.second
                val loadedNoteEntities: List<NoteEntity> = it.first
                if (isCached)
                    transCacheNoteEntityToDomain(loadedNoteEntities)
                else
                    loadCacheNoteEntitiesToDomain(queryEntity)
            }
    }

    /*override fun searchNotes(query: Query): Flowable<List<Note>> {
        val queryEntity = queryMapper.mapToEntity(query)
        return factory.retrieveRemoteDataStore().searchNotes(queryEntity)
            .doOnNext { if (it.isNotEmpty()) saveNotes(it) }
            .mergeWith(factory.retrieveCacheDataStore().searchNotes(queryEntity))
            .flatMapIterable { it }
            .distinct()
            .map { noteMapper.mapFromEntity(it) }
            .toList()
            .toFlowable()

    }*/

    private fun loadCacheNoteEntitiesToDomain(queryEntity: QueryEntity): Flowable<List<Note>> {
        return factory.retrieveCacheDataStore().searchNotes(queryEntity)
            .map { cachedNoteEntities ->
                cachedNoteEntities.map { noteMapper.mapFromEntity(it) }
            }
    }

    private fun transCacheNoteEntityToDomain(cacheEntities: List<NoteEntity>): Flowable<List<Note>>{
        return Flowable.just(cacheEntities.map { noteMapper.mapFromEntity(it) })
    }

    private fun saveNotes(noteEntities: List<NoteEntity>, page: Int): Completable {
        return factory.retrieveCacheDataStore().saveNotes(noteEntities, page)
    }

}