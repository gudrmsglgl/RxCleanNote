package com.cleannote.ui

import android.content.SharedPreferences
import androidx.annotation.IdRes
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.bumptech.glide.RequestManager
import com.cleannote.TestNoteFragmentFactory
import com.cleannote.app.R
import com.cleannote.common.DateUtil
import com.cleannote.domain.Constants
import com.cleannote.notedetail.edit.NoteDetailEditFragment
import com.cleannote.notelist.NoteListFragment
import com.cleannote.test.NoteFactory
import com.cleannote.test.QueryFactory
import com.cleannote.ui.base.BaseTest
import com.cleannote.ui.base.NoteItem
import com.cleannote.ui.screen.DetailEditNoteScreen
import com.cleannote.ui.screen.NoteListScreen
import io.mockk.every
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NavFragmentTest: BaseTest() {

    val screenNoteList = NoteListScreen
    val screenDetailEdit = DetailEditNoteScreen

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory
    @Inject
    lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var reqManager: RequestManager
    @Inject
    lateinit var dateUtil: DateUtil

    init {
        injectTest()
    }

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun noteListItemClickThenNavDetailView_onAndroid(){
        val stubNotes = NoteFactory.makeNotes(10, cacheOrder())
        val query = QueryFactory.makeQuery(cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(stubNotes), query)

        launchNoteListFragmentInContainerNavController()

        screenNoteList {
            recyclerView.firstItem<NoteItem> {
                click()
            }
        }
        navController.isCurDestId(R.id.noteDetailViewFragment)
    }

    @Test
    fun detailEditBackIcThenNavDetailView_onAndroid(){
        stubNoteRepositoryDelete()
        launchDetailEditFragmentInContainerNavController()
        navController.isCurDestId(R.id.noteDetailEditFragment)
        screenDetailEdit {
            toolbar.primaryMenu.click()
        }
        navController.isCurDestId(R.id.noteDetailViewFragment)
    }

    @Test
    fun detailEditDeleteNoteThenNavNoteList_onAndroid(){
        stubNoteRepositoryDelete()
        launchDetailEditFragmentInContainerNavController()
        screenDetailEdit {
            toolbar.secondMenu {
                hasDrawable(R.drawable.ic_delete_24dp)
                click()
            }
            deleteDialog.positiveBtn.click()
            idle(1000)
        }
        navController.isNotCurDestId(R.id.noteDetailEditFragment)
    }

    @Test
    fun detailEditDeleteNoteErrorThenNotNavDetailView_onAndroid(){
        stubThrowableNoteRepositoryDelete(RuntimeException())
        launchDetailEditFragmentInContainerNavController()
        screenDetailEdit {
            toolbar.secondMenu {
                hasDrawable(R.drawable.ic_delete_24dp)
                click()
            }
            errorDialog.positiveBtn.click()
        }
        navController.isCurDestId(R.id.noteDetailEditFragment)
    }

    private fun launchNoteListFragmentInContainerNavController(){
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_app_graph)
        navController.setCurrentDestination(R.id.noteListFragment)
        launchFragmentInContainer {
            NoteListFragment(
                viewModelFactory,
                sharedPref
            ).also { fragment ->
                fragment.setUIController(mockUIController)
                fragment.viewLifecycleOwnerLiveData.observeForever { lifecycleOwner ->
                    if (lifecycleOwner != null) {
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }
    }

    private fun launchDetailEditFragmentInContainerNavController(){
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_detail_graph)
        navController.setCurrentDestination(R.id.noteDetailEditFragment)
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
    }

    override fun setupUIController() {
        every { mockUIController.isDisplayProgressBar() }.returns(false)
        fragmentFactory.uiController = mockUIController
    }

    override fun injectTest() {
        getComponent().inject(this)
    }

    private fun cacheOrder() = sharedPref.getString(
        Constants.FILTER_ORDERING_KEY,
        Constants.ORDER_DESC
    ) ?: Constants.ORDER_DESC

    private fun TestNavHostController.isCurDestId(@IdRes destIdRes: Int){
        assertThat(currentDestination?.id, `is`(destIdRes))
    }

    private fun TestNavHostController.isNotCurDestId(@IdRes destIdRes: Int){
        assertThat(currentDestination?.id, not(`is`(destIdRes)))
    }
}