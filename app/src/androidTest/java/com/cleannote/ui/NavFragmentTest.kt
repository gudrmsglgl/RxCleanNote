package com.cleannote.ui

import android.content.SharedPreferences
import androidx.annotation.IdRes
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.cleannote.app.R
import com.cleannote.domain.Constants
import com.cleannote.notelist.NoteListFragment
import com.cleannote.test.NoteFactory
import com.cleannote.test.QueryFactory
import com.cleannote.ui.screen.NoteListScreen
import io.mockk.every
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
class NavFragmentTest: BaseTest() {

    val screenNoteList = NoteListScreen

    @Inject
    lateinit var fragmentFactory: TestNoteFragmentFactory
    @Inject
    lateinit var sharedPref: SharedPreferences

    init {
        injectTest()
    }

    @Before
    fun setup(){
        setupUIController()
    }

    @Test
    fun noteListItemClickThenNavDetailView_onAndroid(){
        navController.setGraph(R.navigation.nav_app_graph)
        val stubNotes = NoteFactory.makeNotes(10, cacheOrder())
        val query = QueryFactory.makeQuery(cacheOrder())
        stubNextPageExist(false)
        stubNoteRepositorySearchNotes(Single.just(stubNotes), query)

        launchFragmentInContainer<NoteListFragment>(factory = fragmentFactory)
            .onFragment {
                Navigation.setViewNavController(it.requireView(), navController)
            }

        screenNoteList {
            recyclerView.firstItem<NoteItem> {
                click()
            }
        }

        navController.verifyCurrentDestination(R.id.noteDetailViewFragment)
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

    private fun TestNavHostController.verifyCurrentDestination(@IdRes destIdRes: Int){
        assertThat(currentDestination?.id, `is`(destIdRes))
    }
}