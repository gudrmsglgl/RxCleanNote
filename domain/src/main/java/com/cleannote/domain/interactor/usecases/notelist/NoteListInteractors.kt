package com.cleannote.domain.interactor.usecases.notelist

import javax.inject.Inject

open class NoteListInteractors
@Inject constructor(
    val insertNewNote: InsertNewNote,
    val getNumNotes: GetNumNotes
){
    fun disPoses(){
        insertNewNote.dispose()
        getNumNotes.dispose()
    }
}