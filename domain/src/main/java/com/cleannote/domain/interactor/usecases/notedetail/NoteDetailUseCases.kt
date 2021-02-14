package com.cleannote.domain.interactor.usecases.notedetail

import com.cleannote.domain.interactor.UseCaseManager
import com.cleannote.domain.interactor.usecases.common.DeleteNote
import javax.inject.Inject

class NoteDetailUseCases
@Inject
constructor(
    val updateNote: UpdateNote,
    val deleteNote: DeleteNote
): UseCaseManager(updateNote, deleteNote)