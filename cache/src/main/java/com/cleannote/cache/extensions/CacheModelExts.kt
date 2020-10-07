package com.cleannote.cache.extensions

import com.cleannote.cache.model.CacheNoteImage
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.model.CachedNoteImages
import com.cleannote.data.model.NoteEntity
import com.cleannote.data.model.NoteImageEntity

fun CacheNoteImage.transEntity(): NoteImageEntity = NoteImageEntity(
    this.img_pk, this.note_pk, this.image_path
)

fun NoteImageEntity.transCache(): CacheNoteImage = CacheNoteImage(
    this.img_pk, this.note_pk, this.img_path
)

fun NoteEntity.divideCacheNote(): CachedNote = CachedNote(
    this.id, this.title, this.body, this.updated_at, this.created_at
)

fun NoteEntity.divideCacheNoteImages(): List<CacheNoteImage> = this.images?.map { it.transCache() }?: emptyList()

fun CachedNoteImages.transEntity(): NoteEntity = NoteEntity(
    this.cachedNote.id, this.cachedNote.title, this.cachedNote.body,
    this.cachedNote.updated_at, this.cachedNote.created_at ,
    this.images.map { it.transEntity() }
)