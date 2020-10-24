/*
package com.cleannote.ui

import androidx.test.core.app.ActivityScenario
import com.cleannote.HEspresso.recycler.NRecyclerItem
import com.cleannote.MainActivity
import com.cleannote.app.R
import com.cleannote.notelist.NoteListAdapter
import com.cleannote.test.NoteFactory
import com.cleannote.test.QueryFactory
import com.cleannote.ui.screen.MainActivityScreen
import io.mockk.every
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

class NotesNavigationTest: BaseTest() {

    init {
        injectTest()
    }

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    val mainActivityScreen = MainActivityScreen

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun noteListNavDetailFragment(){
        val notes = NoteFactory.makeNotes(0,5)
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubNoteRepositoryUpdate()

        ActivityScenario.launch(MainActivity::class.java)
        mainActivityScreen {
            noteListScreen {
                recyclerView {
                    firstItem<NRecyclerItem<NoteListAdapter.NoteViewHolder>> {
                        click()
                    }
                }
            }
        }
    }

    @Test
    fun noteDetailPrimaryMenuOfBackThenNavNoteList(){
        val notes = NoteFactory.makeNotes(0,5)
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubNoteRepositoryUpdate()

        ActivityScenario.launch(MainActivity::class.java)
        mainActivityScreen {
            noteListScreen.recyclerView{
                firstItem<NRecyclerItem<NoteListAdapter.NoteViewHolder>> {
                    click()
                }
            }
            noteDetailScreen.toolbar{
                primaryMenu.click()
            }
        }
    }

    @Test
    fun detailFragmentTitleUpdateThenNoteListTitleUpdated(){
        val notes = NoteFactory.makeNotes(0,5)
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubNoteRepositoryUpdate()

        ActivityScenario.launch(MainActivity::class.java)
        val updateText = "updatedTest"
        mainActivityScreen {
            noteListScreen {
                recyclerView {
                    firstItem<NRecyclerItem<NoteListAdapter.NoteViewHolder>> {
                        click()
                    }
                }
            }
            noteDetailScreen {
                noteTitle {
                    typeText(updateText)
                }
                toolbar {
                    secondMenu.hasDrawable(R.drawable.ic_done_24dp)
                    secondMenu.click()
                    pressBack()
                }
            }
            noteListScreen.recyclerView {
                firstItem<NRecyclerItem<NoteListAdapter.NoteViewHolder>> {
                    itemTitle {
                        containText(updateText)
                    }
                }
            }
        }
    }

    @Test
    fun detailFragmentSecondaryMenuOfDeleteThenNoteListDeleteItemUpdated(){
        val notes = NoteFactory.makeNotes(5,0)
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubNoteRepositoryDelete()

        ActivityScenario.launch(MainActivity::class.java)

        mainActivityScreen {
            noteListScreen {
                recyclerView {
                    firstItem<NRecyclerItem<NoteListAdapter.NoteViewHolder>> {
                        click()
                    }
                }
            }
            noteDetailScreen {
                toolbar {
                    secondMenu.hasDrawable(R.drawable.ic_delete_24dp)
                    secondMenu.click()
                    deleteDialog {
                        title.hasText(R.string.delete_title)
                        positiveBtn.click()
                    }
                    deleteSuccessToast {
                        isDisplayed()
                    }
                }
            }
            noteListScreen.recyclerView {
                hasSize(notes.size.minus(1))
                firstItem<NRecyclerItem<NoteListAdapter.NoteViewHolder>> {
                    itemTitle.hasText(notes[1].title)
                }
            }
        }
    }

    @Test
    fun detailFragmentSecondaryMenuOfDeleteErrorThenDontNavListFragment(){
        val errorMsg = "TestRuntimeError"
        val notes = NoteFactory.makeNotes(5,0)
        val query = QueryFactory.makeQuery()
        stubInitOrdering(query.order)
        stubNoteRepositorySearchNotes(Flowable.just(notes), query)
        stubThrowableNoteRepositoryDelete(RuntimeException(errorMsg))

        ActivityScenario.launch(MainActivity::class.java)

        mainActivityScreen {
            noteListScreen {
                recyclerView {
                    firstItem<NRecyclerItem<NoteListAdapter.NoteViewHolder>> {
                        click()
                    }
                }
            }
            noteDetailScreen {
                toolbar {
                    secondMenu.hasDrawable(R.drawable.ic_delete_24dp)
                    secondMenu.click()
                    deleteDialog {
                        title.hasText(R.string.delete_title)
                        positiveBtn.click()
                    }
                    deleteErrorToast{
                        isDisplayed()
                    }
                }
            }
        }
    }

    override fun setupUIController() {
        every { mockUIController.isDisplayProgressBar() }.returns(false)
        fragmentFactory.uiController = mockUIController
    }

    override fun injectTest() {
        getComponent().inject(this)
    }
}*/
