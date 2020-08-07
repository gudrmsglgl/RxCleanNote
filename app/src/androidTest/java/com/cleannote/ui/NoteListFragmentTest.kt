package com.cleannote.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.common.UIController
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.HEspresso.NoteListScreen
import com.cleannote.HEspresso.recycler.NRecyclerItem
import com.cleannote.app.R
import com.cleannote.injection.TestApplicationComponent
import com.cleannote.notelist.NoteListAdapter
import com.cleannote.notelist.NoteListAdapter.NoteViewHolder
import com.cleannote.notelist.NoteListFragment
import com.cleannote.test.NoteFactory
import com.cleannote.test.QueryFactory
import com.cleannote.test.util.EspressoIdlingResourceRule
import com.cleannote.test.util.RecyclerViewMatcher
import com.cleannote.test.util.SchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NoteListFragmentTest: BaseTest() {

    @get: Rule
    val schedulerRule = SchedulerRule()

    @get: Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @get: Rule
    val espressoIdlingResourceRule = EspressoIdlingResourceRule()

    val screen = NoteListScreen

    init {
        injectTest()
    }

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    val mockUIController: UIController = mockk(relaxUnitFun = true)

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

        screen {
            recyclerView {
                isNotDisplayed()
            }

            noDataTextView {
                isVisible()
                hasText(R.string.recyclerview_no_data)
            }
        }
    }

    @Test
    fun notesDisplayed(){
        val notes = NoteFactory.makeNotes(1,11)
        stubNoteRepositoryGetNotes(Flowable.just(notes), QueryFactory.makeQuery())

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {

            recyclerView {
                isDisplayed()
                hasSize(notes.size)

                firstItem<NRecyclerItem<NoteViewHolder>> {
                    itemTitle {
                        hasText(notes[0].title)
                    }
                }

                lastItem<NRecyclerItem<NoteViewHolder>> {
                    itemTitle {
                        hasText(notes[notes.lastIndex].title)
                    }
                }
            }

            noDataTextView {
                isGone()
            }
        }

    }

    @Test
    fun filterDialogDisplayed(){
        val notes = NoteFactory.makeNotes(0, 10)
        stubNoteRepositoryGetNotes(Flowable.just(notes), QueryFactory.makeQuery())

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {

            toolbar {

                filterMenu {
                    isDisplayed()
                    hasDrawable(R.drawable.ic_filter_list_grey_24dp)
                    click()
                }

                filterDialog {
                    mainTitle {
                        isDisplayed()
                        hasText(R.string.filter_title)
                    }
                    subTitle {
                        isDisplayed()
                        hasText(R.string.filter_desc)
                    }
                    radioBtnAsc {
                        click()
                        isChecked()
                    }
                    radioBtnDesc {
                        isNotChecked()
                    }
                    pressBack()
                    idle(500L)
                    doesNotExist()
                }

            }
        }

    }


    private fun setupUIController() = with(fragmentFactory){
        uiController = mockUIController
    }

    private fun stubNoteRepositoryGetNotes(data: Flowable<List<Note>>, query: Query? = null) {
        every {
            getComponent().provideNoteRepository().searchNotes(query ?: any())
        } returns data
    }

    override fun injectTest() {
        (application.applicationComponent as TestApplicationComponent)
            .inject(this)
    }
}