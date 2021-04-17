package com.cleannote.ui

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.test.espresso.IdlingRegistry
import com.bumptech.glide.RequestManager
import com.cleannote.DataBindingIdlingResource
import com.cleannote.TestNoteFragmentFactory
import com.cleannote.app.R
import com.cleannote.model.NoteUiModel
import com.cleannote.monitorFragment
import com.cleannote.notedetail.Keys.GLIDE_DETAIL_VIEW_STATE_KEY
import com.cleannote.notedetail.Keys.IS_EXECUTE_INSERT
import com.cleannote.notedetail.Keys.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.view.GlideLoadState
import com.cleannote.notedetail.view.NoteDetailViewFragment
import com.cleannote.test.NoteFactory
import com.cleannote.ui.base.BaseTest
import com.cleannote.ui.screen.DetailViewScreen
import io.mockk.every
import org.junit.After
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

class NoteDetailViewFragmentTest : BaseTest() {

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var reqManager: RequestManager

    val screenDetailView = DetailViewScreen

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    init {
        injectTest()
    }

    @Before
    fun setup() {
        setupUIController()
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregister() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun startDetailViewDisplay_onAndroid() {
        val note = NoteFactory.defaultNote()
        launchDetailViewFragment(note)

        screenDetailView {
            appbar.toolbar {
                homeIcon.isDisplayed()
                title.hasEmptyText()
                editIcon.isDisplayed()
            }
            emptyImage.isGone()
            headerViewPager {
                firstItem {
                    image {
                        isDisplayed()
                        idle(1000L) // for glide load complete
                        glideLoadState(
                            GLIDE_DETAIL_VIEW_STATE_KEY,
                            GlideLoadState.STATE_SUCCESS
                        )
                    }
                }
            }
            body {
                title.hasText(note.title)
                contentContainer.content.hasText(note.body)
                updateTime.hasText(note.updatedAt)
            }
        }
    }

    @Test
    fun detailViewImgLoadFail_onAndroid() {
        val note = NoteFactory.errorImageNote()
        launchDetailViewFragment(note)

        screenDetailView {
            headerViewPager {
                firstItem {
                    image {
                        isDisplayed()
                        idle(1000L) // for glide load complete
                        glideLoadState(
                            GLIDE_DETAIL_VIEW_STATE_KEY,
                            GlideLoadState.STATE_FAIL
                        )
                        hasDrawable(R.drawable.error)
                    }
                }
            }
        }
    }

    @Test
    fun detailViewImgNull_onAndroid() {
        val note = NoteFactory.emptyImageNote()
        launchDetailViewFragment(note)

        screenDetailView {
            headerViewPager.isGone()
            emptyImage.isDisplayed()
        }
    }

    private fun launchDetailViewFragment(note: NoteUiModel) {
        val stubBundle = Bundle().apply {
            putParcelable(NOTE_DETAIL_BUNDLE_KEY, note)
            putBoolean(IS_EXECUTE_INSERT, false)
        }

        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_detail_graph)

        val scenario = launchFragmentInContainer(fragmentArgs = stubBundle) {
            NoteDetailViewFragment(viewModelFactory, reqManager).also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { lifecycleOwner ->
                    fragment.setUIController(mockUIController)
                    if (lifecycleOwner != null) {
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        dataBindingIdlingResource.monitorFragment(scenario)
    }

    override fun setupUIController() {
        every { mockUIController.isDisplayProgressBar() } returns false
        fragmentFactory.uiController = mockUIController
    }

    override fun injectTest() {
        getComponent().inject(this)
    }
}
