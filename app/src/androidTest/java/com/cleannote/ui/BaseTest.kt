package com.cleannote.ui

import androidx.navigation.NavController
import androidx.test.core.app.ApplicationProvider
import com.cleannote.HEspresso.recycler.NRecyclerItem
import com.cleannote.TestBaseApplication
import com.cleannote.common.UIController
import com.cleannote.domain.Constants
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.injection.TestApplicationComponent
import com.cleannote.notelist.NoteListAdapter
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Flowable

abstract class BaseTest {
    val mockUIController: UIController = mockk(relaxUnitFun = true)
    val navController = mockk<NavController>(relaxed = true)

    val application: TestBaseApplication
        = ApplicationProvider.getApplicationContext() as TestBaseApplication

    fun getComponent(): TestApplicationComponent {
        return application.applicationComponent as TestApplicationComponent
    }

    fun stubInitOrdering(order: String) = every {
        getComponent()
            .provideSharedPreferences()
            .getString(Constants.FILTER_ORDERING_KEY, Constants.ORDER_DESC)
    }.returns(order)

    fun stubNoteRepositorySearchNotes(data: Flowable<List<Note>>, query: Query? = null) {
        every {
            getComponent().provideNoteRepository().searchNotes(query ?: any())
        } returns data
    }

    fun stubThrowableNoteRepositorySearchNotes(throwable: Throwable, query: Query? = null) {
        every {
            getComponent().provideNoteRepository().searchNotes(query ?: any())
        } returns Flowable.error(throwable)
    }

    fun stubSaveOrdering(order: String) = every {
        getComponent()
            .provideSharedPreferences()
            .edit()
            .putString(any(), any())
            .apply()
    } just Runs

    fun stubNoteRepositoryUpdate(){
        every {
            getComponent().provideNoteRepository().updateNote(any())
        }.returns(Completable.complete())
    }

    fun stubThrowableNoteRepositoryUpdate(throwable: Throwable){
        every {
            getComponent().provideNoteRepository().updateNote(any())
        }.returns(Completable.error(throwable))
    }

    fun stubNoteRepositoryDelete(){
        every {
            getComponent().provideNoteRepository().deleteNote(any())
        }.returns(Completable.complete())
    }

    fun stubThrowableNoteRepositoryDelete(throwable: Throwable){
        every {
            getComponent().provideNoteRepository().deleteNote(any())
        }.returns(Completable.error(throwable))
    }

    fun stubNoteRepositoryDeleteMultiNotes(){
        every {
            getComponent().provideNoteRepository().deleteMultipleNotes(any())
        }.returns(Completable.complete())
    }

    fun stubThrowableNoteRepositoryDeleteMultiNotes(throwable: Throwable){
        every {
            getComponent().provideNoteRepository().deleteMultipleNotes(any())
        }.returns(Completable.error(throwable))
    }

    abstract fun setupUIController()
    abstract fun injectTest()
}

typealias RecyclerItem = NRecyclerItem<NoteListAdapter.NoteViewHolder>