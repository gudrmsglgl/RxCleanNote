package com.cleannote.presentation.notedetail

import com.cleannote.domain.interactor.usecases.common.DeleteNote
import com.cleannote.domain.interactor.usecases.notedetail.NoteDetailUseCases
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.presentation.test.InstantExecutorExtension
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
open class NoteDetailViewModelTest {

    lateinit var usecases: NoteDetailUseCases
    lateinit var viewModel: NoteDetailViewModel

    lateinit var deleteNote: DeleteNote
    lateinit var updateNote: UpdateNote

    @BeforeEach
    fun detailVMTestSetup() {
        useCaseMock()
        usecases = NoteDetailUseCases(updateNote, deleteNote)
        viewModel = NoteDetailViewModel(usecases)
    }

    private fun useCaseMock() {
        deleteNote = mock()
        updateNote = mock()
    }
}
