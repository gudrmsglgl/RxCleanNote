package com.cleannote.ui

import android.content.SharedPreferences
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.MainActivity
import com.cleannote.ui.screen.MainActivityScreen
import com.cleannote.ui.screen.NoteListScreen
import com.cleannote.app.R
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
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
    val screenNoteList = NoteListScreen

    init {
        injectTest()
    }

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun searchNotesEmptyThenNoteNotDisplayed_onAndroid(){
        val query = QueryFactory.makeQuery(cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(emptyList()), query)

        launchFragmentInContainer<NoteListFragment>(
            factory = fragmentFactory
        )

        screenNoteList {
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
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        val query = QueryFactory.makeQuery(cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
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
        val query = QueryFactory.makeQuery(cacheOrder())
        stubNextPageExist(false)
        stubThrowableNoteRepositorySearchNotes(RuntimeException(errorMsg), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
            errorDialog {
                title.hasText(R.string.dialog_title_error)
                message.hasText(R.string.searchErrorMsg)
                positiveBtn.click()
            }
        }
    }

    @Test
    fun filterDialogDisplayedOfCheckedCacheOrder_onAndroid(){
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        val query = QueryFactory.makeQuery(cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
            searchToolbar {
                filterMenu {
                    isDisplayed()
                    hasDrawable(R.drawable.ic_filter_list_grey_24dp)
                    click()
                }
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

                if (cacheOrder() == ORDER_DESC)
                    radioBtnDesc.isChecked()
                else
                    radioBtnAsc.isChecked()

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

    @Test
    fun filterSetOrderingThenReturnSortingNotes_onAndroid(){
        val defaultNotes = NoteFactory.makeNotes(10, cacheOrder())
        val defaultQuery =  QueryFactory.makeQuery(cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(defaultNotes), defaultQuery)

        val orderedNotes = NoteFactory.makeNotes(10, cacheOrderReverse())
        val orderQuery = QueryFactory.makeQuery(cacheOrderReverse())
        stubNoteRepositorySearchNotes(Single.just(orderedNotes), orderQuery)
        
        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
            searchToolbar{
                filterMenu {
                    click()
                }
            }

            filterDialog {
                if (cacheOrder() == ORDER_DESC)
                    radioBtnAsc.click()
                else
                    radioBtnDesc.click()

                sortBtn.click()
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
        val query = QueryFactory.makeQuery(cacheOrder())
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        val keyword = "searchNote"
        val searchQuery = QueryFactory.makeQuery(cacheOrder()).apply { like = keyword }
        val note = NoteFactory.makeNote(title = "searchText", date = "03")
        val searchedNotes = listOf(note)
        stubNoteRepositorySearchNotes(Single.just(searchedNotes), searchQuery)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
            searchToolbar {
                searchView {
                    searchBtn.click()
                    searchEditView {
                        searchText(keyword)
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
        val query = QueryFactory.makeQuery(cacheOrder())
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        val text = "empty"
        val searchQuery = QueryFactory.makeQuery(cacheOrder()).apply { like = text }
        val emptyNotes = emptyList<Note>()
        stubNoteRepositorySearchNotes(Single.just(emptyNotes), searchQuery)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
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
        val stubNotes = NoteFactory.makeNotes(20, cacheOrder())

        val initQuery = QueryFactory.makeQuery(cacheOrder())
        val notes = stubNotes.subList(0, 9)
        stubNextPageExist(true)
        stubNoteRepositorySearchNotes(Single.just(notes), initQuery)

        val nextQuery = QueryFactory.makeQuery(cacheOrder()).apply { page = 2 }
        val nextNotes = stubNotes.subList(10, 19)
        stubNoteRepositorySearchNotes(Single.just(nextNotes), nextQuery)

        val endQuery = QueryFactory.makeQuery(cacheOrder()).apply { page = 3 }
        val endNotes: List<Note> = emptyList()
        stubNoteRepositorySearchNotes(Single.just(endNotes), endQuery)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
            recyclerView {
                swipeUp()
                hasSize(notes.size + nextNotes.size)
                scrollToEnd()
                visibleLastItem<NoteItem> {
                    itemTitle {
                        hasText(nextNotes[getLastVisiblePosition()-notes.size].title) // nextNote[0~9], so minus notes.size: 19 - 10
                    }
                }
            }
        }
    }

    @Test
    fun createNoteThenInputTitleDialog_onAndroid(){
        val query = QueryFactory.makeQuery(cacheOrder())
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        ActivityScenario.launch(MainActivity::class.java)

        activity{
            screenNoteList {
                insertBtn {
                    click()
                }
                newNoteDialog {
                    message {
                        hasText(R.string.dialog_new_note)
                    }
                    inputTitle {
                        typeText("FabTest")
                    }
                }
            }
        }
    }

    @Test
    fun recyclerViewSwipeLeftThenVisibleDeleteMenu_onAndroid(){
        val query = QueryFactory.makeQuery(cacheOrder())
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
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
        val query = QueryFactory.makeQuery(cacheOrder()).apply { startIndex = null }
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)
        stubNoteRepositoryDelete()

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
            recyclerView{
                firstItem<NoteItem> {
                    swipeLeft()
                    swipeDeleteMenu.click()
                }
            }
            deleteDialog {
                positiveBtn.click()
            }
            deleteSuccessToast.isDisplayed()
            recyclerView.hasSize(notes.size.minus(1))
        }
    }

    @Test
    fun swipeDeleteMenuFailThenNotDeleteNotes_onAndroid(){
        val query = QueryFactory.makeQuery(cacheOrder())
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)
        stubThrowableNoteRepositoryDelete(RuntimeException())

        ActivityScenario.launch(MainActivity::class.java)

        activity {
            screenNoteList {
                recyclerView{
                    firstItem<NoteItem> {
                        swipeLeft()
                        swipeDeleteMenu.click()
                    }
                }
                deleteDialog {
                    positiveBtn.click()
                }
                errorDialog {
                    title.hasText(R.string.dialog_title_error)
                    positiveBtn.click()
                }
                recyclerView.hasSize(notes.size)
            }
        }
    }

    @Test
    fun noteLongClickThenMultiSelectStateToolbar_onAndroid(){
        val query = QueryFactory.makeQuery(cacheOrder())
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
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
        val query = QueryFactory.makeQuery(cacheOrder())
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(notes), query)
        stubThrowableNoteRepositoryDeleteMultiNotes(RuntimeException())

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
            recyclerView {
                firstItem<NoteItem> {
                    longClick()
                }
                firstItem<NoteItem> {
                    click()
                    checkBox {
                        isDisplayed()
                        isChecked()
                    }
                }
                visibleLastItem<NoteItem> {
                    click()
                    checkBox {
                        isDisplayed()
                        isChecked()
                    }
                }
            }
            multiDeleteToolbar {
                btnConfirm.click()
            }
            deleteDialog {
                positiveBtn.click()
            }
            errorDialog {
                title.hasText(R.string.dialog_title_error)
                message.hasText(R.string.deleteErrorMsg)
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
        val query = QueryFactory.makeQuery(cacheOrder())
        val notes = NoteFactory.makeNotes(10, cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositoryDeleteMultiNotes()

        // b/c when vm multiDelete then initNotes -> recall searchNotes()
        val deletedNotes = NoteFactory.makeNotes(8, cacheOrder())
        stubNoteRepositorySearchNotes(
            query,
            Single.just(notes),
            Single.just(deletedNotes)
        )

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)

        screenNoteList {
            recyclerView {
                firstItem<NoteItem> {
                    longClick()
                }
                firstItem<NoteItem> {
                    itemTitle.hasText(notes[0].title)
                    click()
                    checkBox{
                        isDisplayed()
                        isChecked()
                    }
                }
                childAt<NoteItem>(1){
                    click()
                    checkBox{
                        isDisplayed()
                        isChecked()
                    }
                }
                checkSize = getCheckedSize()
            }
            multiDeleteToolbar {
                btnConfirm.click()
            }
            deleteDialog {
                positiveBtn.click()
            }
            searchToolbar {
                isDisplayed()
            }
            idle(5000L)
            recyclerView {
                firstItem<NoteItem> {
                    itemTitle.hasText(deletedNotes[0].title)
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

    private fun cacheOrder() = sharedPref.getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC

    private fun cacheOrderReverse(): String{
        return if (cacheOrder() == ORDER_DESC)
            ORDER_ASC
        else
            ORDER_DESC
    }
}