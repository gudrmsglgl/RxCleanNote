package com.cleannote.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.ui.screen.MainActivityScreen
import com.cleannote.ui.screen.NoteListScreen
import com.cleannote.MainActivity
import com.cleannote.app.R
import com.cleannote.domain.Constants.ORDER_ASC
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.model.Note
import com.cleannote.notelist.NoteListFragment
import com.cleannote.test.NoteFactory
import com.cleannote.test.QueryFactory
import com.cleannote.test.util.EspressoIdlingResourceRule
import io.mockk.every
import io.reactivex.Single
import org.junit.*
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NoteListFragmentTest: BaseTest() {

    @get: Rule
    val espressoIdlingResourceRule = EspressoIdlingResourceRule()

    val activity = MainActivityScreen
    val noteListScreen = NoteListScreen

    init {
        injectTest()
    }

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun searchNotesEmptyThenNoteNotDisplayed_onAndroid(){
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(emptyList()), query)

        launchFragmentInContainer<NoteListFragment>(
            factory = fragmentFactory
        )

        noteListScreen {
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
    fun searchNoteSuccessThenNotesDisplayed_onAndroid(){
        val notes = NoteFactory.makeNotes(1,11)
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {

            recyclerView {
                isDisplayed()
                hasSize(notes.size)

                firstItem<NoteItem> {
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
    fun searchNotesThrowableThenErrorDialogMessage_onAndroid(){
        val errorMsg = "Test Error"
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubThrowableNoteRepositorySearchNotes(RuntimeException(errorMsg), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            errorDialog {
                title.hasText(R.string.dialog_title_error)
                message.hasText(R.string.searchErrorMsg)
                positiveBtn.click()
            }
        }
    }

    @Test
    fun filterDialogDisplayed_onAndroid(){
        val notes = NoteFactory.makeNotes(0, 10)
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)
        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {

            searchToolbar {

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
    fun filterSetOrderingDESCThenReturnSearchNotesDESC_onAndroid(){
        val defaultNotes = NoteFactory.makeNotes(0, 10)
        val defaultQuery =  QueryFactory.makeQuery().apply { order = ORDER_ASC }
        stubInitOrdering(defaultQuery.order)
        stubNoteRepositorySearchNotes(Single.just(defaultNotes), defaultQuery)

        val orderedNotes = NoteFactory.makeNotes(10,0)
        val orderQuery = QueryFactory.makeQuery().apply { order = ORDER_DESC }
        stubSaveOrdering(orderQuery.order)
        stubNoteRepositorySearchNotes(Single.just(orderedNotes), orderQuery)
        
        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            searchToolbar{
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
                firstItem<NoteItem> {
                    itemTitle {
                        hasText(orderedNotes[0].title)
                    }
                }
                visibleLastItem<NoteItem> {
                    itemTitle {
                        hasText(orderedNotes[getLastVisiblePosition()].title)
                    }
                }
            }
        }
    }

    @Test
    fun searchTextQueryThenReturnSearchedNotes_onAndroid(){
        val query = QueryFactory.makeQuery()
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        val text = "searchNote"
        val searchQuery = QueryFactory.makeQuery().apply { like = text }
        val note = NoteFactory.makeNote(title = "searchText", date = "03")
        val searchedNotes = listOf(note)
        stubNoteRepositorySearchNotes(Single.just(searchedNotes), searchQuery)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            searchToolbar {
                searchView {
                    searchBtn.click()
                    searchEditView {
                        searchText(text)
                    }
                }
                idle(1500) // for RxDebounce(1000)
            }
            recyclerView {
                firstItem<NoteItem> {
                    itemTitle {
                        hasText(searchedNotes[0].title)
                    }
                }
            }
        }
    }

    @Test
    fun searchTextQueryEmptyNotesThenNoDataTextView_onAndroid(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        val text = "empty"
        val searchQuery = QueryFactory.makeQuery().apply { order = ORDER_ASC; like = text }
        val emptyNotes = emptyList<Note>()
        stubNoteRepositorySearchNotes(Single.just(emptyNotes), searchQuery)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            searchToolbar {
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
    fun scrollRecyclerViewReturnNextNotes_onAndroid(){
        val initQuery = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(initQuery.order)
        stubNoteRepositorySearchNotes(Single.just(notes), initQuery)

        val nextQuery = QueryFactory.makeQuery().apply {
            order = ORDER_ASC
            page = 2 }
        val nextNotes = NoteFactory.makeNotes(10,20)
        stubNoteRepositorySearchNotes(Single.just(nextNotes), nextQuery)

        val endQuery = QueryFactory.makeQuery().apply {
            order = ORDER_ASC
            page = 3 }
        val endNotes: List<Note> = emptyList()
        stubNoteRepositorySearchNotes(Single.just(endNotes), endQuery)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            recyclerView {
                idle(1000)
                swipeUp()
                hasSize(notes.size + nextNotes.size)
                scrollToEnd()
                visibleLastItem<NoteItem> {
                    itemTitle {
                        hasText(nextNotes[getLastVisiblePosition()-notes.size].title) // nextNote[0~9], so minus notes.size: 19 - 10
                    }
                }
                idle(3000)
            }
        }
    }

    @Test
    fun clickFabDisplayInputDialog_onAndroid(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            insertBtn {
                click()
            }
            newNoteDialog {
                message {
                    hasText(R.string.dialog_new_note)
                }
                editTitle {
                    hasHint(R.string.dialog_new_note_hint)
                    typeText("FabTest")
                }
            }
        }

    }

    @Test
    fun recyclerViewSwipeLeftThenVisibleDeleteMenu_onAndroid(){
        val query = QueryFactory.makeQuery()
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            recyclerView {
                firstItem<NoteItem> {
                    swipeLeft()
                    swipeDeleteMenu {
                        isDisplayed()
                        deleteImg.hasDrawable(R.drawable.ic_delete_24dp)
                    }
                }
            }
        }
    }

    @Test
    fun swipeDeleteMenuSuccessThenNotesDelete_onAndroid(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)
        stubNoteRepositoryDelete()

        ActivityScenario.launch(MainActivity::class.java)

        noteListScreen {
            recyclerView{
                firstItem<NoteItem> {
                    swipeLeft()
                    swipeDeleteMenu.click()
                }
            }
            deleteDialog {
                title.hasText(R.string.delete_title)
                positiveBtn.click()
            }
            deleteSuccessToast.isDisplayed()
            recyclerView.hasSize(notes.size.minus(1))
        }
    }

    @Test
    fun swipeDeleteMenuFailThenNotDeleteNotes_onAndroid(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)
        stubThrowableNoteRepositoryDelete(RuntimeException())

        ActivityScenario.launch(MainActivity::class.java)

        activity {
            noteListScreen {
                recyclerView{
                    firstItem<NoteItem> {
                        swipeLeft()
                        swipeDeleteMenu.click()
                    }
                }
                deleteDialog {
                    title.hasText(R.string.delete_title)
                    positiveBtn.click()
                }
                errorDialog {
                    title.hasText(R.string.dialog_title_warning)
                    positiveBtn.click()
                }
                recyclerView.hasSize(notes.size)
            }
        }
    }

    @Test
    fun noteLongClickThenMultiSelectStateToolbar_onAndroid(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            recyclerView {
                firstItem<NoteItem> {
                    longClick()
                }
            }
            multiDeleteToolbar {
                isDisplayed()
                btnCancel {
                    isDisplayed()
                    hasDrawable(R.drawable.ic_cancel_24dp)
                }
                title.hasText(R.string.tb_multi_delete_title)
                btnConfirm {
                    isDisplayed()
                    hasDrawable(R.drawable.ic_done_24dp)
                }
            }
        }
    }

    @Test
    fun multiDeleteThrowableThenSearchStateDefaultNotes_onAndroid(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)
        stubThrowableNoteRepositoryDeleteMultiNotes(RuntimeException())
        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            recyclerView {
                firstItem<NoteItem> {
                    longClick()
                }
                firstItem<NoteItem> {
                    click()
                    checkBox{
                        isDisplayed()
                        isChecked()
                    }
                }
                visibleLastItem<NoteItem> {
                    click()
                    checkBox{
                        isDisplayed()
                        isChecked()
                    }
                }
            }
            multiDeleteToolbar {
                isDisplayed()
                btnCancel {
                    isDisplayed()
                    hasDrawable(R.drawable.ic_cancel_24dp)
                }
                title.hasText(R.string.tb_multi_delete_title)
                btnConfirm {
                    isDisplayed()
                    hasDrawable(R.drawable.ic_done_24dp)
                    click()
                }
            }
            deleteDialog {
                positiveBtn.click()
            }
            searchToolbar {
                isDisplayed()
            }
            recyclerView {
                firstItem<NoteItem> {
                    checkBox.isNotDisplayed()
                }
            }
        }
    }

    @Test
    fun multiDeleteSuccessThenSearchStateDefaultNotesOfCheckDeleted_onAndroid(){
        var checkSize = 0
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Single.just(notes), query)
        stubNoteRepositoryDeleteMultiNotes()
        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        noteListScreen {
            recyclerView {
                firstItem<NoteItem> {
                    longClick()
                }
                firstItem<NoteItem> {
                    click()
                    checkBox{
                        isDisplayed()
                        isChecked()
                    }
                }
                visibleLastItem<NoteItem> {
                    click()
                    checkBox{
                        isDisplayed()
                        isChecked()
                    }
                }
                checkSize = getCheckedSize()
            }
            multiDeleteToolbar {
                isDisplayed()
                btnConfirm {
                    isDisplayed()
                    hasDrawable(R.drawable.ic_done_24dp)
                    click()
                }
            }
            deleteDialog {
                positiveBtn.click()
            }
            searchToolbar {
                isDisplayed()
            }
            recyclerView {
                firstItem<NoteItem> {
                    checkBox.isNotDisplayed()
                }
                hasSize(notes.size - checkSize)
            }
        }
    }


    override fun setupUIController(){
        every { mockUIController.isDisplayProgressBar() }.returns(false)
        fragmentFactory.uiController = mockUIController
    }

    override fun injectTest() {
        getComponent().inject(this)
    }
}