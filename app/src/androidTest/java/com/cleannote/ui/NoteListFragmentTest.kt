package com.cleannote.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.HEspresso.NoteListScreen
import com.cleannote.HEspresso.recycler.NRecyclerItem
import com.cleannote.app.R
import com.cleannote.common.UIController
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.injection.TestApplicationComponent
import com.cleannote.notelist.NoteListAdapter.NoteViewHolder
import com.cleannote.notelist.NoteListFragment
import com.cleannote.test.NoteFactory
import com.cleannote.test.QueryFactory
import com.cleannote.test.util.EspressoIdlingResourceRule
import com.cleannote.test.util.SchedulerRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
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
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositoryGetNotes(Flowable.just(emptyList()), query)

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
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositoryGetNotes(Flowable.just(notes), query)

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
            }

            noDataTextView {
                isGone()
            }
        }

    }

    @Test
    fun filterDialogDisplayed(){
        val notes = NoteFactory.makeNotes(0, 10)
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        stubInitOrdering(query.order)
        stubNoteRepositoryGetNotes(Flowable.just(notes), query)
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
                    sortBtn {
                        isDisplayed()
                        hasText(R.string.filter_btn_ok)
                    }
                    pressBack()
                    idle(500L)
                    doesNotExist()
                }

            }
        }

    }

    @Test
    fun filterOrderingDESC(){
        val defaultNotes = NoteFactory.makeNotes(0, 10)
        val defaultQuery =  QueryFactory.makeQuery().apply { order = ORDER_ASC }
        stubInitOrdering(defaultQuery.order)
        stubNoteRepositoryGetNotes(Flowable.just(defaultNotes), defaultQuery)

        val orderedNotes = NoteFactory.makeNotes(10,0)
        val orderQuery = QueryFactory.makeQuery().apply { order = ORDER_DESC }
        stubSaveOrdering(orderQuery.order)
        stubNoteRepositoryGetNotes(Flowable.just(orderedNotes), orderQuery)
        
        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {
            toolbar{
                filterMenu {
                    click()
                }
                filterDialog {
                    radioBtnDesc.click()
                    sortBtn.click()
                }
            }
            recyclerView {
                hasSize(orderedNotes.size)
                firstItem<NRecyclerItem<NoteViewHolder>> {
                    itemTitle {
                        hasText(orderedNotes[0].title)
                    }
                }
                visibleLastItem<NRecyclerItem<NoteViewHolder>> {
                    itemTitle {
                        hasText(orderedNotes[getLastVisiblePosition()].title)
                    }
                }
            }
        }
    }

    @Test
    fun searchViewSetQueryReturnNotes(){
        val query = QueryFactory.makeQuery()
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositoryGetNotes(Flowable.just(notes), query)

        val text = "searchNote"
        val searchQuery = QueryFactory.makeQuery().apply { like = text }
        val note = NoteFactory.makeNote(title = "searchText", date = "03")
        val searchedNotes = listOf(note)
        stubNoteRepositoryGetNotes(Flowable.just(searchedNotes), searchQuery)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {
            toolbar {
                searchView {
                    searchBtn.click()
                    searchEditView {
                        searchText(text)
                    }
                }
                idle(1500) // for RxDebounce(1000)
            }
            recyclerView {
                firstItem<NRecyclerItem<NoteViewHolder>> {
                    itemTitle {
                        hasText(searchedNotes[0].title)
                    }
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

    private fun stubInitOrdering(order: String) = every {
        getComponent()
            .provideSharedPreferences()
            .getString(FILTER_ORDERING_KEY, ORDER_DESC)
    }.returns(order)

    private fun stubSaveOrdering(order: String) = every {
        getComponent()
            .provideSharedPreferences()
            .edit()
            .putString(any(), any())
            .apply()
    } just Runs

    override fun injectTest() {
        (application.applicationComponent as TestApplicationComponent)
            .inject(this)
    }
}