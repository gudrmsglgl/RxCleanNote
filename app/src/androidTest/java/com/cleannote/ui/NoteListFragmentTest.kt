package com.cleannote.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.app.R
import com.cleannote.common.UIController
import com.cleannote.domain.model.Note
import com.cleannote.injection.TestApplicationComponent
import com.cleannote.notelist.NoteListFragment
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NoteListFragmentTest: BaseTest() {

    init {
        injectTest()
    }

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    val mockUIController: UIController = mock()

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun emptyNoteNotDisplayed(){
        stubNoteRepositoryGetNotes(Flowable.just(emptyList()))

        val scenario = launchFragmentInContainer<NoteListFragment>(
            factory = fragmentFactory
        )

        onView(withId(R.id.recycler_view))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.tv_no_data))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    private fun setupUIController() = with(fragmentFactory){
        uiController = mockUIController
    }

    private fun stubNoteRepositoryGetNotes(data: Flowable<List<Note>>) {
        whenever(getComponent().provideNoteRepository().searchNotes(any()))
            .thenReturn(data)
    }

    override fun injectTest() {
        (application.applicationComponent as TestApplicationComponent)
            .inject(this)
    }
}