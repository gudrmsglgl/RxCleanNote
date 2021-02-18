package com.cleannote.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.bumptech.glide.RequestManager
import com.cleannote.MainActivity
import com.cleannote.app.R
import com.cleannote.common.DateUtil
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.edit.NoteDetailEditFragment
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.cleannote.test.NoteFactory
import com.cleannote.ui.screen.DetailNoteScreen
import com.cleannote.ui.screen.MainActivityScreen
import io.mockk.every
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NoteDetailEditFragmentTest: BaseTest() {

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var reqManager: RequestManager

    @Inject
    lateinit var dateUtil: DateUtil

    val screen = DetailNoteScreen
    private val note: NoteUiModel

    init {
        injectTest()
        note = NoteFactory.testNote()
    }

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun noteDetailDisplayed(){
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_detail_graph)

        launchFragmentInContainer {
            NoteDetailEditFragment(
                viewModelFactory,
                dateUtil,
                reqManager
            ).also { fragment ->
                fragment.setUIController(mockUIController)
                fragment.viewLifecycleOwnerLiveData.observeForever { lifecycleOwner ->
                    if  (lifecycleOwner != null) {
                        Navigation.setViewNavController(fragment.requireView(), navController)
                        fragment.initNoteDefaultMode()
                    }
                }
            }
        }

        screen {
            toolbar {
                primaryMenu {
                    hasDrawable(R.drawable.ic_arrow_back_24dp)
                }
                secondMenu {
                    hasDrawable(R.drawable.ic_delete_24dp)
                }
            }
            noteTitle {
                hasText(note.title)
            }
            scrollview {
                body {
                    hasHint(R.string.detail_note_hint)
                    hasText(note.body)
                }
                idle(1500)
            }
        }
    }

    /*@Test
    fun noteTitleCollapseExpandedThenToolbarSetTitle(){
        launchFragmentInContainer<NoteDetailEditFragment>(
            factory = fragmentFactory,
            fragmentArgs = bundleOf(NOTE_DETAIL_BUNDLE_KEY to note)
        )
        screen {
            noteTitle {
                hasText(note.title)
            }
            scrollview.body.swipeUp()
            toolbar.toolbarTitle.hasText(note.title)
            scrollview.body.swipeDown()
            noteTitle.hasText(note.title)
        }
    }

    @Test
    fun noteTitleEditModeThenChangeIconMenu(){
        launchFragmentInContainer<NoteDetailEditFragment>(
            factory = fragmentFactory,
            fragmentArgs = bundleOf(NOTE_DETAIL_BUNDLE_KEY to note)
        )
        screen {
            noteTitle.typeText("test")
            toolbar {
                primaryMenu.hasDrawable(R.drawable.ic_cancel_24dp)
                secondMenu.hasDrawable(R.drawable.ic_done_24dp)
            }
        }
    }

    @Test
    fun noteTitleEditDoneThenChangeIconMenu(){
        stubNoteRepositoryUpdate()

        launchFragmentInContainer<NoteDetailEditFragment>(
            factory = fragmentFactory,
            fragmentArgs = bundleOf(NOTE_DETAIL_BUNDLE_KEY to note)
        )
        screen {
            noteTitle.typeText("test")
            toolbar {
                secondMenu.click()
                secondMenu.hasDrawable(R.drawable.ic_delete_24dp)
            }
            noteTitle.isFocused(false)
            noteTitle.containText("test")
            updateSuccessToast.isDisplayed()
        }
    }

    @Test
    fun noteTitleEditDoneThenThrowableNotUpdatedTitle(){
        val throwable = RuntimeException()
        val updateText = "test"
        stubThrowableNoteRepositoryUpdate(throwable)
        launchFragmentInContainer<NoteDetailEditFragment>(
            factory = fragmentFactory,
            fragmentArgs = bundleOf(NOTE_DETAIL_BUNDLE_KEY to note)
        )
        screen {
            noteTitle.typeText(updateText)
            toolbar {
                secondMenu.click()
                secondMenu.hasDrawable(R.drawable.ic_delete_24dp)
            }
            noteTitle{
                isFocused(false)
                notContainText(updateText)
            }
            updateErrorToast.isDisplayed()
            idle(3500)
        }
    }

    @Test
    fun noteDeleteSuccessThenNavNoteList(){
        stubNoteRepositoryDelete()
        launchFragmentInContainer<NoteDetailEditFragment>(
            factory = fragmentFactory,
            fragmentArgs = bundleOf(NOTE_DETAIL_BUNDLE_KEY to note)
        ).onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), testNavHostController)
        }
        screen {
            toolbar {
                secondMenu.hasDrawable(R.drawable.ic_delete_24dp)
                secondMenu.click()
                deleteDialog.positiveBtn.click()
            }
            deleteSuccessToast.isDisplayed()
        }
        verify { testNavHostController.popBackStack() }
    }

    @Test
    fun noteDeleteErrorThenNotNavNoteList(){
        stubThrowableNoteRepositoryDelete(RuntimeException())
        launchFragmentInContainer<NoteDetailEditFragment>(
            factory = fragmentFactory,
            fragmentArgs = bundleOf(NOTE_DETAIL_BUNDLE_KEY to note)
        ).onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), testNavHostController)
        }
        screen {
            toolbar {
                secondMenu.hasDrawable(R.drawable.ic_delete_24dp)
                secondMenu.click()
                deleteDialog.positiveBtn.click()
            }
            deleteErrorToast.isDisplayed()
        }
        verify(exactly = 0){ testNavHostController.popBackStack() }
    }

    @Test
    fun noteTitleUpdateCancelThenRevertText(){
        launchFragmentInContainer<NoteDetailEditFragment>(
            factory = fragmentFactory,
            fragmentArgs = bundleOf(NOTE_DETAIL_BUNDLE_KEY to note)
        )
        screen {
            noteTitle.typeText("test")
            toolbar {
                primaryMenu.click()
                primaryMenu.hasDrawable(R.drawable.ic_arrow_back_24dp)
            }
            noteTitle.isFocused(false)
            noteTitle.hasText(titleText)
        }
    }*/

    override fun injectTest() {
        getComponent().inject(this)
    }

    override fun setupUIController(){
        every { mockUIController.isDisplayProgressBar() }.returns(false)
        fragmentFactory.uiController = mockUIController
    }
}