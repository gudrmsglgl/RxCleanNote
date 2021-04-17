package com.cleannote.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.bumptech.glide.RequestManager
import com.cleannote.TestNoteFragmentFactory
import com.cleannote.app.R
import com.cleannote.common.DateUtil
import com.cleannote.model.NoteUiModel
import com.cleannote.notedetail.edit.NoteDetailEditFragment
import com.cleannote.test.NoteFactory
import com.cleannote.ui.base.BaseTest
import com.cleannote.ui.screen.DetailEditNoteScreen
import io.mockk.every
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NoteDetailEditFragmentTest : BaseTest() {

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var reqManager: RequestManager

    @Inject
    lateinit var dateUtil: DateUtil

    private val screenDetailEdit = DetailEditNoteScreen
    private val note: NoteUiModel = NoteFactory.defaultNote()

    init {
        injectTest()
    }

    @Before
    fun setup() {
        setupUIController()
        launchFragmentInContainerNavController()
    }

    @Test
    fun noteDetailEditDisplay_onAndroid() {
        screenDetailEdit {
            toolbar {
                primaryMenu {
                    hasDrawable(R.drawable.ic_arrow_back_24dp)
                }
                secondMenu {
                    hasDrawable(R.drawable.ic_delete_24dp)
                }
            }
            editTitle {
                hasText(note.title)
            }
            scrollview {
                body {
                    hasHint(R.string.detail_note_hint)
                    hasText(note.body)
                }
                footer {
                    imageRcv.firstItem {
                        img.isDisplayed()
                    }
                }
                idle(1500)
            }
        }
    }

    @Test
    fun appBarCollapseThenToolbarTitleVisible_onAndroid() {
        screenDetailEdit {
            scrollview.body.swipeUp()
            toolbar.toolbarTitle.hasText(note.title)
            editTitle.stateCollapse()
        }
    }

    @Test
    fun appBarExpandedThenEditTitleVisible_onAndroid() {
        screenDetailEdit {
            scrollview.body {
                swipeUp()
                swipeDown()
            }
            toolbar.toolbarTitle.isGone()
            editTitle {
                stateExpanded()
                hasText(note.title)
            }
        }
    }

    @Test
    fun editTitleTypeTextThenTbMenuChangeUpdateMenu_onAndroid() {
        screenDetailEdit {
            editTitle.typeText("test")
            toolbar {
                primaryMenu.hasDrawable(R.drawable.ic_cancel_24dp)
                secondMenu.hasDrawable(R.drawable.ic_done_24dp)
            }
        }
    }

    @Test
    fun titleUpdateThenEditTitleContainTypeText_onAndroid() {
        stubNoteRepositoryUpdate()
        val updateTitle = "updatedTitle"
        screenDetailEdit {
            editTitle.typeText(updateTitle)
            toolbar.secondMenu.click()
            editTitle.containText(updateTitle)
        }
    }

    @Test
    fun titleUpdateAndCollapseThenTbTitleUpdated_onAndroid() {
        stubNoteRepositoryUpdate()
        val updateTitle = "updatedTitle"
        screenDetailEdit {
            editTitle.typeText(updateTitle)
            toolbar.secondMenu.click()
            scrollview.body.swipeUp()
            editTitle.stateCollapse()
            toolbar.toolbarTitle.containText(updateTitle)
        }
    }

    @Test
    fun titleUpdateCancelThenNotContainUpdateText_onAndroid() {
        val updateTitle = "updatedTitle"
        screenDetailEdit {
            editTitle.typeText(updateTitle)
            toolbar.primaryMenu.click()
            editTitle.notContainText(updateTitle)
        }
    }

    @Test
    fun titleUpdateErrorThenNotContainUpdateText_onAndroid() {
        stubThrowableNoteRepositoryUpdate(RuntimeException())
        val updateTitle = "updatedTitle"
        screenDetailEdit {
            editTitle.typeText(updateTitle)
            toolbar.secondMenu {
                hasDrawable(R.drawable.ic_done_24dp)
                click()
            }
            errorDialog {
                title.hasText(R.string.dialog_title_error)
                message.hasText(R.string.updateErrorMsg)
                positiveBtn.click()
            }
            editTitle.notContainText(updateTitle)
        }
    }

    @Test
    fun bodyUpdateThenContainTypeText_onAndroid() {
        val updateBody = "updatedBody"
        stubNoteRepositoryUpdate()
        screenDetailEdit {
            scrollview.body.typeText(updateBody)
            toolbar.secondMenu.click()
            scrollview.body.containText(updateBody)
        }
    }

    @Test
    fun bodyUpdateCancelThenNotContainTypeText_onAndroid() {
        val updateBody = "updatedBody"
        stubNoteRepositoryUpdate()
        screenDetailEdit {
            scrollview.body.typeText(updateBody)
            toolbar.primaryMenu.click()
            scrollview.body.notContainText(updateBody)
        }
    }

    @Test
    fun bodyUpdateErrorThenNotContainUpdateText_onAndroid() {
        stubThrowableNoteRepositoryUpdate(RuntimeException())
        val updateBody = "updatedBody"
        screenDetailEdit {
            scrollview.body.typeText(updateBody)
            toolbar.secondMenu {
                hasDrawable(R.drawable.ic_done_24dp)
                click()
            }
            errorDialog {
                title.hasText(R.string.dialog_title_error)
                message.hasText(R.string.updateErrorMsg)
                positiveBtn.click()
            }
            scrollview.body.notContainText(updateBody)
        }
    }

    @Test
    fun footerPopupMenuDisplay_onAndroid() {
        screenDetailEdit {
            footer.popupMenu {
                click()
                cameraMenu.isDisplayed()
                albumMenu.isDisplayed()
                linkMenu.isDisplayed()
            }
        }
    }

    @Test
    fun attachImgDeleteClickThenShowDeleteDialog_onAndroid() {
        screenDetailEdit {
            footer.imageRcv {
                firstItem {
                    img.icDelete.click()
                }
            }
            deleteDialog {
                title.isDisplayed()
            }
        }
    }

    @Test
    fun attachImgDeleteThenTvNoImgDisplay_onAndroid() {
        stubNoteRepositoryUpdate()
        screenDetailEdit {
            footer.imageRcv {
                firstItem {
                    img.icDelete.click()
                }
            }
            deleteDialog {
                positiveBtn.click()
            }
            updateSuccessToast.isDisplayed()
            footer.tvNoImages.isDisplayed()
        }
    }

    private fun launchFragmentInContainerNavController() {
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
                    if (lifecycleOwner != null) {
                        Navigation.setViewNavController(fragment.requireView(), navController)
                        fragment.initNoteDefaultMode()
                    }
                }
            }
        }
    }

    override fun injectTest() {
        getComponent().inject(this)
    }

    override fun setupUIController() {
        every { mockUIController.isDisplayProgressBar() }.returns(false)
        fragmentFactory.uiController = mockUIController
    }
}
