package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import com.cleannote.domain.Constants
import com.cleannote.domain.interactor.usecases.common.DeleteNote
import com.cleannote.domain.interactor.usecases.notelist.DeleteMultipleNotes
import com.cleannote.domain.interactor.usecases.notelist.InsertNewNote
import com.cleannote.domain.interactor.usecases.notelist.NextPageExist
import com.cleannote.domain.interactor.usecases.notelist.NoteListUseCases
import com.cleannote.domain.interactor.usecases.notelist.SearchNotes
import com.cleannote.presentation.test.InstantExecutorExtension
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
open class NoteListViewModelTest {

    protected lateinit var noteListViewModel: NoteListViewModel
    lateinit var useCases: NoteListUseCases
    lateinit var sharedPreferences: SharedPreferences

    lateinit var searchNotes: SearchNotes
    private lateinit var insertNewNote: InsertNewNote
    private lateinit var deleteNote: DeleteNote
    private lateinit var deleteMultipleNotes: DeleteMultipleNotes

    lateinit var nextPageExist: NextPageExist
    private lateinit var queryManager: QueryManager

    @BeforeEach
    fun noteListViewModelSetup() {
        initMock()
        useCases = NoteListUseCases(searchNotes, insertNewNote, deleteNote, deleteMultipleNotes)
        queryManager = QueryManager(sharedPreferences, nextPageExist)
        noteListViewModel = NoteListViewModel(useCases, queryManager)
    }

    private fun initMock() {
        sharedPreferences = mock {
            on { getString(Constants.FILTER_ORDERING_KEY, Constants.ORDER_DESC) } doReturn Constants.ORDER_DESC
        }
        searchNotes = mock()
        insertNewNote = mock()
        deleteNote = mock()
        deleteMultipleNotes = mock()
        nextPageExist = mock()
    }
}
