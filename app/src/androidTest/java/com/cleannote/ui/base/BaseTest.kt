package com.cleannote.ui.base

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.cleannote.TestBaseApplication
import com.cleannote.common.UIController
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.espresso.recycler.list.NRecyclerItem
import com.cleannote.injection.TestApplicationComponent
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.holder.BaseHolder
import io.mockk.*

abstract class BaseTest : RepoStubber {

    override val repository: NoteRepository
        get() = getComponent().provideNoteRepository()

    val mockUIController: UIController = mockk(relaxUnitFun = true)
    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private val application: TestBaseApplication =
        ApplicationProvider.getApplicationContext() as TestBaseApplication

    fun getComponent(): TestApplicationComponent {
        return application.applicationComponent as TestApplicationComponent
    }

    abstract fun setupUIController()
    abstract fun injectTest()
}

typealias NoteItem = NRecyclerItem<BaseHolder<NoteUiModel>>
