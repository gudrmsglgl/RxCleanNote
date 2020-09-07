package com.cleannote.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.ui.screen.MainActivityScreen
import com.cleannote.ui.screen.NoteListScreen
import com.cleannote.HEspresso.recycler.NRecyclerItem
import com.cleannote.MainActivity
import com.cleannote.app.R
import com.cleannote.common.UIController
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.model.Note
import com.cleannote.domain.model.Query
import com.cleannote.notelist.NoteListAdapter.NoteViewHolder
import com.cleannote.notelist.NoteListFragment
import com.cleannote.test.NoteFactory
import com.cleannote.test.QueryFactory
import com.cleannote.test.util.EspressoIdlingResourceRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.reactivex.Flowable
import org.junit.*
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NoteListFragmentTest: BaseTest() {

    @get: Rule
    val espressoIdlingResourceRule = EspressoIdlingResourceRule()

    val activity = MainActivityScreen
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
        stubNoteRepositorySearchNotes(Flowable.just(emptyList()), query)

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
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)

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
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
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
        stubNoteRepositorySearchNotes(Flowable.just(defaultNotes), defaultQuery)

        val orderedNotes = NoteFactory.makeNotes(10,0)
        val orderQuery = QueryFactory.makeQuery().apply { order = ORDER_DESC }
        stubSaveOrdering(orderQuery.order)
        stubNoteRepositorySearchNotes(Flowable.just(orderedNotes), orderQuery)
        
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
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)

        val text = "searchNote"
        val searchQuery = QueryFactory.makeQuery().apply { like = text }
        val note = NoteFactory.makeNote(title = "searchText", date = "03")
        val searchedNotes = listOf(note)
        stubNoteRepositorySearchNotes(Flowable.just(searchedNotes), searchQuery)

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

    @Test
    fun searchViewSetQueryReturnNoData(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)

        val text = "empty"
        val searchQuery = QueryFactory.makeQuery().apply { order = ORDER_ASC; like = text }
        val emptyNotes = emptyList<Note>()
        stubNoteRepositorySearchNotes(Flowable.just(emptyNotes), searchQuery)

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
                isGone()
            }
            noDataTextView {
                isVisible()
                hasText(R.string.recyclerview_no_data)
            }
        }
    }

    @Test
    fun swipeAbleRecyclerView(){
        val query = QueryFactory.makeQuery()
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {
            recyclerView {
                firstItem<NRecyclerItem<NoteViewHolder>> {
                    swipeLeft()
                    swipeDeleteMode {
                        deleteImg.hasDrawable(R.drawable.ic_delete_24dp)
                        deleteText.hasText(R.string.item_menu_delete)
                    }
                }
            }
        }
    }

    @Test
    fun scrollRecyclerViewReturnNextNotes(){
        val initQuery = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(initQuery.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), initQuery)

        val nextQuery = QueryFactory.makeQuery().apply {
            order = ORDER_ASC
            page = 2 }
        val nextNotes = NoteFactory.makeNotes(10,20)
        stubNoteRepositorySearchNotes(Flowable.just(nextNotes), nextQuery)

        val endQuery = QueryFactory.makeQuery().apply {
            order = ORDER_ASC
            page = 3 }
        val endNotes: List<Note> = emptyList()
        stubNoteRepositorySearchNotes(Flowable.just(endNotes), endQuery)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {
            recyclerView {
                idle(1000)
                swipeUp()
                hasSize(notes.size + nextNotes.size)
                scrollToEnd()
                visibleLastItem<NRecyclerItem<NoteViewHolder>> {
                    itemTitle {
                        hasText(nextNotes[getLastVisiblePosition()-notes.size].title) // nextNote[0~9], so minus notes.size: 19 - 10
                    }
                }
                idle(3000)
            }
        }
    }

    @Test
    fun clickFabDisplayInputDialog(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)

        ActivityScenario.launch(MainActivity::class.java)

        activity {
            noteListScreen {
                insertBtn {
                    click()
                }
            }
            newNoteDialog {
                message {
                    hasText(R.string.dialog_newnote)
                }
                editTitle {
                    hasHint(R.string.dialog_newnote_hint)
                    typeText("FabTest")
                }
            }
        }
    }

    private fun setupUIController() = with(fragmentFactory){
        uiController = mockUIController
    }

    private fun stubNoteRepositorySearchNotes(data: Flowable<List<Note>>, query: Query? = null) {
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
        getComponent().inject(this)
    }
}