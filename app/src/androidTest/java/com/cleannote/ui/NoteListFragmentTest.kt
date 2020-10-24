package com.cleannote.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.ui.screen.MainActivityScreen
import com.cleannote.ui.screen.NoteListScreen
import com.cleannote.HEspresso.recycler.NRecyclerItem
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

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun searchNotesEmptyThenNoteNotDisplayed(){
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(emptyList()), query)

        launchFragmentInContainer<NoteListFragment>(
            factory = fragmentFactory
        )

        screen {
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
    fun searchNoteSuccessThenNotesDisplayed(){
        val notes = NoteFactory.makeNotes(1,11)
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {

            recyclerView {
                isDisplayed()
                hasSize(notes.size)

                firstItem<RecyclerItem> {
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
    fun notesThrowableThenErrorDialogMessage(){
        val errorMsg = "Test Error"
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubThrowableNoteRepositorySearchNotes(RuntimeException(errorMsg), query)

        ActivityScenario.launch(MainActivity::class.java)

        activity {
            errorDialog {
                title.hasText(R.string.dialog_title_warning)
                positiveBtn.click()
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
                firstItem<RecyclerItem> {
                    itemTitle {
                        hasText(orderedNotes[0].title)
                    }
                }
                visibleLastItem<RecyclerItem> {
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
                firstItem<RecyclerItem> {
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
    fun swipeAbleRecyclerView(){
        val query = QueryFactory.makeQuery()
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {
            recyclerView {
                firstItem<RecyclerItem> {
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
                visibleLastItem<RecyclerItem> {
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

    @Test
    fun noteSwipeSingleDeleteSuccessThenNotesDelete(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubNoteRepositoryDelete()

        ActivityScenario.launch(MainActivity::class.java)

        screen {
            recyclerView{
                firstItem<RecyclerItem> {
                    swipeLeft()
                    swipeDeleteMode {
                        deleteImg.click()
                    }
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
    fun noteSwipeSingleDeleteErrorThenDontDeleteNotes(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubThrowableNoteRepositoryDelete(RuntimeException())

        ActivityScenario.launch(MainActivity::class.java)

        activity {
            noteListScreen {
                recyclerView{
                    firstItem<RecyclerItem> {
                        swipeRight()
                        swipeDeleteMode {
                            deleteImg.click()
                        }
                    }
                }
                deleteDialog {
                    title.hasText(R.string.delete_title)
                    positiveBtn.click()
                }
            }

            errorDialog {
                title.hasText(R.string.dialog_title_warning)
                positiveBtn.click()
            }

            noteListScreen.recyclerView.hasSize(notes.size)
        }
    }

    @Test
    fun noteLongClickThenMultiSelectStateToolbar(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {
            recyclerView {
                firstItem<RecyclerItem> {
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
    fun multiDeleteThrowableThenSearchStateDefaultNotes(){
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubThrowableNoteRepositoryDeleteMultiNotes(RuntimeException())
        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {
            recyclerView {
                firstItem<RecyclerItem> {
                    longClick()
                }
                firstItem<RecyclerItem> {
                    click()
                    checkBox{
                        isDisplayed()
                        isChecked()
                    }
                }
                visibleLastItem<RecyclerItem> {
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
                firstItem<RecyclerItem> {
                    checkBox.isNotDisplayed()
                }
            }
        }
    }

    @Test
    fun multiDeleteSuccessThenSearchStateDefaultNotesOfCheckDeleted(){
        var checkSize = 0
        val query = QueryFactory.makeQuery().apply { order = ORDER_ASC }
        val notes = NoteFactory.makeNotes(0, 10)
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubNoteRepositoryDeleteMultiNotes()
        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screen {
            recyclerView {
                firstItem<RecyclerItem> {
                    longClick()
                }
                firstItem<RecyclerItem> {
                    click()
                    checkBox{
                        isDisplayed()
                        isChecked()
                    }
                }
                visibleLastItem<RecyclerItem> {
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
                firstItem<RecyclerItem> {
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