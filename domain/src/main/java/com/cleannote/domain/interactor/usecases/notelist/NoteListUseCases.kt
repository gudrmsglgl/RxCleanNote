package com.cleannote.domain.interactor.usecases.notelist

import com.cleannote.domain.interactor.UseCaseManager
import com.cleannote.domain.interactor.usecases.common.DeleteNote
import javax.inject.Inject

class NoteListUseCases
@Inject
constructor(
    val searchNotes: SearchNotes,
    val insertNewNote: InsertNewNote,
    val deleteNote: DeleteNote,
    val deleteMultipleNotes: DeleteMultipleNotes
) : UseCaseManager(
    searchNotes, insertNewNote, deleteNote, deleteMultipleNotes
)
