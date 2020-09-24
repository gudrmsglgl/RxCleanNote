package com.cleannote.ui

import android.view.View
import androidx.navigation.NavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.TestBaseApplication
import com.cleannote.common.UIController
import com.cleannote.domain.Constants
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.injection.TestApplicationComponent
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Flowable
import org.hamcrest.Matcher

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

    abstract fun setupUIController()
    abstract fun injectTest()
}