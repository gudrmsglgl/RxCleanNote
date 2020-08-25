package com.cleannote.ui

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.app.R
import com.cleannote.common.UIController
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.NoteDetailFragment
import com.cleannote.test.NoteFactory
import com.cleannote.ui.screen.DetailNoteScreen
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NoteDetailFragmentTest: BaseTest() {

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    val mockUIController = mockk<UIController>(relaxUnitFun = true)
    val screen = DetailNoteScreen
    private val note: NoteUiModel

    init {
        injectTest()
        note = NoteFactory.makeNoteUiModel(title = "testTitle",body = "testBody", date = "20")
    }

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun noteDetailDisplayed(){

        launchFragmentInContainer<NoteDetailFragment>(
            factory = fragmentFactory,
            fragmentArgs = bundleOf(NOTE_DETAIL_BUNDLE_KEY to note)
        )

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

    @Test
    fun noteTitleCollapseExpandedThenToolbarSetTitle(){
        launchFragmentInContainer<NoteDetailFragment>(
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
        launchFragmentInContainer<NoteDetailFragment>(
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

    /*@Test
    fun noteTitleEditDoneThenChangeIconMenu(){
        launchFragmentInContainer<NoteDetailFragment>(
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
        }
    }*/

    override fun injectTest() {
        getComponent().inject(this)
    }

    private fun setupUIController(){
        fragmentFactory.uiController = mockUIController
    }
}