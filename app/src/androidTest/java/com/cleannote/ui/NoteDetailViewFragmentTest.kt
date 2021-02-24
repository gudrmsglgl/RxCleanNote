package com.cleannote.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.IdlingRegistry
import com.bumptech.glide.RequestManager
import com.cleannote.DataBindingIdlingResource
import com.cleannote.app.R
import com.cleannote.monitorFragment
import com.cleannote.notedetail.Keys
import com.cleannote.notedetail.Keys.IS_EXECUTE_INSERT
import com.cleannote.notedetail.Keys.NOTE_DETAIL_BUNDLE_KEY
import com.cleannote.notedetail.view.NoteDetailViewFragment
import com.cleannote.test.NoteFactory
import com.cleannote.ui.screen.DetailViewScreen
import io.mockk.every
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

class NoteDetailViewFragmentTest: BaseTest() {

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var reqManager: RequestManager

    val screenDetailView = DetailViewScreen

    private val dataBindingIdlingResource: DataBindingIdlingResource = DataBindingIdlingResource()

    init {
        injectTest()
    }

    @Before
    fun setup(){
        setupUIController()
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregister(){
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun toolbarIsDisplayed(){

    }

    @Test
    fun scrollDownThenToolbarTitleVisible_onAndroid(){
        val note = NoteFactory.makeNoteUiModel(id = "#1", title = "testTitle", body = "testBody", date = "2021-02-17")
        val stubBundle = Bundle().apply {
            putParcelable(NOTE_DETAIL_BUNDLE_KEY, note)
            putBoolean(IS_EXECUTE_INSERT, false)
        }
        navController.setViewModelStore(ViewModelStore())
        assertThat(navController.backStack.size, `is`(0))
        navController.setGraph(R.navigation.nav_detail_graph)
        assertThat(navController.currentDestination?.id, `is`(R.id.noteDetailViewFragment))
        assertThat(navController.backStack.size, `is`(2))
        navController.setCurrentDestination(R.id.noteDetailViewFragment)
        assertThat(navController.backStack.size, `is`(2))
        assertThat(navController.currentDestination?.id, `is`(R.id.noteDetailViewFragment))

        /*val fragmentScenario = launchFragmentInContainer<NoteDetailViewFragment>(
            factory = fragmentFactory,
            fragmentArgs = stubBundle
        ).onFragment { fragment ->
            fragment.viewLifecycleOwnerLiveData.observeForever { lifecycleOwner ->
                if (lifecycleOwner != null) {
                    Navigation.setViewNavController(fragment.requireView(), navController)
                }
            }
        }

        dataBindingIdlingResource.monitorFragment(fragmentScenario)*/

        /*screenDetailView {
            toolbar {
                homeIcon.isDisplayed()
                title.hasEmptyText()
            }
        }*/
    }

    override fun setupUIController() {
        every { mockUIController.isDisplayProgressBar() } returns false
        fragmentFactory.uiController = mockUIController
    }

    override fun injectTest() {
        getComponent().inject(this)
    }
}