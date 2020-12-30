package com.cleannote.presentation.notedetail.update

import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.State.*
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.notedetail.NewNoteDetailViewModelTest
import com.cleannote.presentation.notedetail.update.tester.UpdateFeatureTester
import com.cleannote.presentation.notedetail.update.tester.UpdateUseCaseCaptors
import com.cleannote.presentation.test.factory.NoteFactory
import com.nhaarman.mockitokotlin2.times
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoteDetailVMUpdateTest: NewNoteDetailViewModelTest() {

    private lateinit var updateFeatureTester: UpdateFeatureTester
    private lateinit var captors: UpdateUseCaseCaptors

    private val defaultNoteView
        get() = NoteFactory.createNoteView(id = "#1",title = "defaultTitle",body = "defaultBody", date = "2020-12-30 08:00:00")

    @BeforeEach
    fun updateTestSetup(){
        captors = UpdateUseCaseCaptors()
        updateFeatureTester = UpdateFeatureTester(viewModel, updateNote, captors)
    }

    @Test
    fun updateNoteExecuteUseCase(){
        val editNoteView = defaultNoteView.copy(title = "updatedTitle", updated_at = "2020-12-30 08:10:00")
        with(updateFeatureTester){

            defaultMode(defaultNoteView)
                .expectNoteMode(TextMode.DefaultMode)
                .editMode()
                .expectNoteMode(TextMode.EditMode)

            editDoneMode(editNoteView)
                .verifyUseCaseExecute()
        }
    }

    @Test
    fun updateNoteStateLoadingReturnNoData(){
        val editNoteView = defaultNoteView.copy(title = "updatedTitle", updated_at = "2020-12-30 08:10:00")
        with(updateFeatureTester){

            defaultMode(defaultNoteView)
                .expectNoteMode(TextMode.DefaultMode)
                .editMode()
                .expectNoteMode(TextMode.EditMode)

            editDoneMode(editNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)
                .expectData(null)

            expectFinalNote(defaultNoteView)
        }
    }

    @Test
    fun updateNoteStateLoadingToSuccessReturnData(){
        val editNoteView = defaultNoteView.copy(title = "updatedTitle", updated_at = "2020-12-30 08:10:00")
        with(updateFeatureTester){

            defaultMode(defaultNoteView)
                .expectNoteMode(TextMode.DefaultMode)
                .editMode()
                .expectNoteMode(TextMode.EditMode)

            editDoneMode(editNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnComplete()
                .verifyChangeState(SUCCESS)
                .expectNoteMode(TextMode.EditDoneMode)
                .expectData(editNoteView)

            expectFinalNote(editNoteView)
        }
    }



    @Test
    fun updateNoteStateErrorReturnThrowableNoData_TriggerNotUpdatedFinalNote(){
        val editNoteView = defaultNoteView.copy(title = "updatedTitle", updated_at = "2020-12-30 08:10:00")
        with(updateFeatureTester){

            defaultMode(defaultNoteView)
                .expectNoteMode(TextMode.DefaultMode)
                .editMode()
                .expectNoteMode(TextMode.EditMode)

            editDoneMode(editNoteView)
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)

            stubUseCaseOnError(RuntimeException())
                .verifyChangeState(ERROR)
                .expectNoteMode(TextMode.EditDoneMode)
                .expectData(null)

            verifyFinalNoteOnChange(defaultNoteView, times(2))
                .expectFinalNote(defaultNoteView)
        }
    }

    @Test
    fun updateNoteEditToEditCancel_ThenDefaultMode_NotExecuteUpdateNote_TriggerFinalNote(){
        val editNoteView = defaultNoteView.copy(title = "updatedTitle", updated_at = "2020-12-30 08:10:00")
        with(updateFeatureTester){

            defaultMode(defaultNoteView)
                .expectNoteMode(TextMode.DefaultMode)
                .editMode()
                .expectNoteMode(TextMode.EditMode)

            editCancel()
                .expectNoteMode(TextMode.DefaultMode)
                .verifyUseCaseNotExecute()

            verifyFinalNoteOnChange(defaultNoteView, times(2))
                .expectFinalNote(defaultNoteView)
        }
    }

    @Test
    fun uploadImageThenUpdateNoteExecute(){
        val imgPath = "https://testUpdateImgPath.co.kr?=adsf.png"
        val updateTime = "2020-12-30 08:10:00"
        with(updateFeatureTester){

            defaultMode(defaultNoteView)
            uploadImage(imgPath, updateTime)
                .verifyUseCaseExecute()

        }
    }

    @Test
    fun uploadImageStateLoadingReturnNoData(){
        val imgPath = "https://testUpdateImgPath.co.kr?=adsf.png"
        val updateTime = "2020-12-30 08:10:00"
        with(updateFeatureTester){

            defaultMode(defaultNoteView)
            uploadImage(imgPath, updateTime)
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)
                .expectData(null)
            expectFinalNote(defaultNoteView)

        }
    }

    @Test
    fun uploadImageStateLoadingToSuccess_ReturnFinalNoteFirstImgPath_UpdatedImagePath(){
        val imgPath = "https://testUpdateImgPath.co.kr?=adsf.png"
        val updateTime = "2020-12-30 08:10:00"

        with(updateFeatureTester){

            defaultMode(defaultNoteView)
            uploadImage(imgPath, updateTime)
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)
                .expectData(null)

            stubUseCaseOnComplete()
                .verifyChangeState(SUCCESS)
                .expectFinalNoteFirstImgPath(imgPath)

        }
    }

    @Test
    fun uploadImageStateErrorReturnThrowableNoData(){
        val imgPath = "https://testUpdateImgPath.co.kr?=adsf.png"
        val updateTime = "2020-12-30 08:10:00"
        val throwable = RuntimeException()

        with(updateFeatureTester){

            defaultMode(defaultNoteView)
            uploadImage(imgPath, updateTime)
                .verifyUseCaseExecute()
                .verifyChangeState(LOADING)
                .expectData(null)

            stubUseCaseOnError(throwable)
                .verifyChangeState(ERROR)
                .expectError(throwable)
                .expectData(null)

            expectFinalNote(defaultNoteView)
        }
    }

}