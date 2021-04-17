package com.cleannote.presentation.notelist.next

import com.cleannote.presentation.data.State
import com.cleannote.presentation.notelist.NoteListViewModelTest
import com.cleannote.presentation.notelist.next.tester.NextFeatureTester
import com.cleannote.presentation.notelist.next.tester.NextUseCaseCaptors
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class NoteListVMNextPageTest : NoteListViewModelTest() {

    private lateinit var captors: NextUseCaseCaptors
    private lateinit var nextFeatureTester: NextFeatureTester

    @BeforeEach
    fun nextPageTestSetup() {
        captors = NextUseCaseCaptors()
        nextFeatureTester = NextFeatureTester(noteListViewModel, nextPageExist, captors)
    }

    @Test
    fun nextPageExecuteUseCase() {
        with(nextFeatureTester) {
            nextPageExist()
                .verifyUseCaseExecute()
        }
    }

    @Test
    fun nextPageStateLoadingReturnNoData() {
        with(nextFeatureTester) {
            nextPageExist()
                .verifyUseCaseExecute()
                .expectState(State.LOADING)
                .expectData(null)
        }
    }

    @Test
    fun nextPageStateLoadingToSuccessReturnData() {
        with(nextFeatureTester) {
            nextPageExist()
                .verifyUseCaseExecute()
                .stubUseCaseOnSuccess(true)
                .expectState(State.SUCCESS)
                .expectData(true)
        }
    }

    @Test
    fun nextPageStateErrorReturnThrowableNoData() {
        with(nextFeatureTester) {
            nextPageExist()
                .verifyUseCaseExecute()
                .stubUseCaseOnError(RuntimeException())
                .expectState(State.ERROR)
                .expectData(null)
        }
    }
}
