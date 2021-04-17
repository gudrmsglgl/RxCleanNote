package com.cleannote.cache.extensions

import com.cleannote.cache.model.CachedImage
import com.cleannote.cache.model.CachedNote
import com.cleannote.cache.model.CachedNoteImages
import com.cleannote.data.model.NoteEntity
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.not
import org.junit.Assert.assertFalse

fun Long.assertGreaterThan(expect: Long) {
    assertThat(this, greaterThan(expect))
}

fun Int.expectNoteSize(expect: Int) {
    assertThat(this, `is`(expect))
}

fun List<CachedImage>.expectImages(param: List<CachedImage>) {
    assertThat(this, `is`(param))
}

fun CachedNoteImages.hasNote(expect: CachedNote): CachedNoteImages {
    assertThat(this.cachedNote, `is`(expect))
    return this
}

fun CachedNoteImages.hasImages(expect: List<CachedImage>): CachedNoteImages {
    assertThat(this.images, `is`(expect))
    return this
}

fun List<CachedNoteImages>.assertSortedValue(expect: List<NoteEntity>) =
    this.zip(expect)
        .forEach {
            val searchedNote = it.first
            val savedNote = it.second

            searchedNote
                .hasNote(savedNote.divideCacheNote())
                .hasImages(savedNote.divideCacheNoteImages())
        }

fun List<CachedNoteImages>.assertValueAt(index: Int, expect: NoteEntity) {
    this[index]
        .hasNote(expect.divideCacheNote())
        .hasImages(expect.divideCacheNoteImages())
}

fun List<CachedNoteImages>.hasNotes(expect: CachedNote): List<CachedNoteImages> {
    assertThat(
        this.map { it.cachedNote },
        not(hasItem(expect))
    )
    return this
}

fun List<CachedNoteImages>.notHasNote(expect: CachedNote): List<CachedNoteImages> {
    assertThat(
        this.map { it.cachedNote },
        not(hasItem(expect))
    )
    return this
}

fun List<CachedNoteImages>.notHasNotes(expectSubList: List<CachedNote>) {
    val allNote = this.map { it.cachedNote }
    assertFalse(expectSubList.stream().allMatch(allNote::contains))
}

fun List<CachedNoteImages>.notHasImages(expectSubList: List<CachedImage>): List<CachedNoteImages> {
    val allImages: List<CachedImage> = this.flatMap { it.images }
    assertFalse(expectSubList.stream().allMatch(allImages::contains))
    return this
}
