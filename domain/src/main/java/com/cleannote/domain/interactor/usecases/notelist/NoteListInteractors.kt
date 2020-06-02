package com.cleannote.domain.interactor.usecases.notelist

class NoteListInteractors(
    val insertNewNote: InsertNewNote,
    val getNumNotes: GetNumNotes
){
    fun disPoses(){
        insertNewNote.dispose()
        getNumNotes.dispose()
    }
}