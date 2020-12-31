package com.cleannote.presentation.notedetail.update.tester

import androidx.lifecycle.Observer
import com.cleannote.domain.interactor.usecases.notedetail.UpdateNote
import com.cleannote.domain.model.Note
import com.cleannote.presentation.ViewModelFeatureTester
import com.cleannote.presentation.data.DataState
import com.cleannote.presentation.data.State
import com.cleannote.presentation.data.notedetail.Mode
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.extensions.verifyExecute
import com.cleannote.presentation.model.NoteView
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.nhaarman.mockitokotlin2.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.verification.VerificationMode

class UpdateFeatureTester(
    val viewModel: NoteDetailViewModel,
    val usecase: UpdateNote,
    val updateCaptors: UpdateUseCaseCaptors
): ViewModelFeatureTester<UpdateFeatureTester, Nothing, Note, NoteView>(updateCaptors) {

    private val mObNoteView: Observer<NoteView> = mock()

    init {
        viewModel.finalNote.removeObserver(mObNoteView)
        viewModel.finalNote.observeForever(mObNoteView)
    }

    fun defaultMode(param: NoteView?): UpdateFeatureTester{
        viewModel.defaultMode(param)
        return this
    }

    fun editMode(): UpdateFeatureTester{
        viewModel.editMode()
        return this
    }

    fun editCancel(): UpdateFeatureTester{
        viewModel.editCancel()
        return this
    }

    fun editDoneMode(param: NoteView): UpdateFeatureTester{
        viewModel.editDoneMode(param)
        setState(currentState())

        return this
    }

    fun uploadImage(path: String, updateTime: String): UpdateFeatureTester{
        viewModel.uploadImage(path, updateTime)
        setState(currentState())
        return this
    }

    fun expectNoteMode(expect: Mode): UpdateFeatureTester{
        assertThat(viewModel.noteMode.value, `is`(expect))
        return this
    }

    fun expectFinalNote(expect: NoteView): UpdateFeatureTester{
        assertThat(finalNote(), `is`(expect))
        return this
    }

    fun expectFinalNoteFirstImgPath(expect: String): UpdateFeatureTester{
        assertThat(finalNote()?.noteImages?.get(0)?.img_path, `is`(expect))
        return this
    }

    private fun finalNote() = viewModel.finalNote()

    override fun verifyUseCaseExecute(): UpdateFeatureTester{
        usecase.verifyExecute(updateCaptors, updateCaptors.param)
        return this
    }

    fun verifyUseCaseNotExecute(): UpdateFeatureTester{
        verify(usecase, never()).execute(any(), any(), any(), any(), any())
        return this
    }

    fun verifyFinalNoteOnChange(
        updatedParam: NoteView,
        verificationMode: VerificationMode?
    ): UpdateFeatureTester{
        verify(mObNoteView, verificationMode ?: times(1)).onChanged(updatedParam)
        return this
    }

    override fun vmCurrentData(): DataState<NoteView>? {
        return viewModel.updatedNote.value
    }

    override fun currentState(): State? {
        return vmCurrentData()?.status
    }
}