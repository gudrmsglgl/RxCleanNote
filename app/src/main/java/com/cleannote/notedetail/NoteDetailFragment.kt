package com.cleannote.notedetail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog

import com.cleannote.app.R
import com.cleannote.app.databinding.FragmentNoteDetailBinding
import com.cleannote.common.BaseFragment
import com.cleannote.common.DateUtil
import com.cleannote.extension.*
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.data.State.ERROR
import com.cleannote.presentation.data.State.SUCCESS
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbCollapse
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbExpanded
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.layout_note_detail_toolbar.*

const val NOTE_DETAIL_BUNDLE_KEY = "com.cleannote.notedetail.select_note"
const val NOTE_DETAIL_DELETE_KEY = "com.cleannote.notedetail.request_onback_delete"
const val REQUEST_KEY_ON_BACK = "com.cleannote.notedetail.request_onback"

class NoteDetailFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil
) : BaseFragment<FragmentNoteDetailBinding>(R.layout.fragment_note_detail) {

    private val COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD = -85

    lateinit var noteUiModel: NoteUiModel
    val viewModel: NoteDetailViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        getPreviousFragmentNote()

        appBarOffSetChangeSource()
        titleBodyChangeSource()

        subscribeUpdateNote()
        subscribeDeleteNote()
    }

    private fun initDataBinding() = with(binding){
        vm = viewModel
        fragment = this@NoteDetailFragment
    }

    private fun subscribeUpdateNote() = viewModel.updatedNote
        .observe(viewLifecycleOwner, Observer {
            if (it != null){
                when (it.status) {
                    is SUCCESS -> {
                        showToast(getString(R.string.updateSuccessMsg))
                    }
                    is ERROR -> {
                        showToast(getString(R.string.updateErrorMsg))
                        it.sendFirebaseThrowable()
                    }
                    else -> {}
                }
            }
        })

    private fun subscribeDeleteNote() = viewModel.deletedNote
        .observe( viewLifecycleOwner, Observer {
            if (it != null){
                when (it.status) {
                    is SUCCESS -> {
                        showToast(getString(R.string.deleteSuccessMsg))
                        navNoteListFragment()
                    }
                    is ERROR -> {
                        it.sendFirebaseThrowable()
                        showToast(getString(R.string.deleteErrorMsg))
                    }
                    else -> {}
                }
            }
        })

    private fun titleBodyChangeSource() = Observable.merge(
        note_title.textChanges().filter { note_title.isFocused  && noteUiModel.title != note_title.text.toString()},
        note_body.textChanges().filter { note_body.isFocused && noteUiModel.body != note_body.text.toString()})
        .subscribe { editMode() }
        .addCompositeDisposable()

    fun showDeleteDialog() {
        activity?.let {
            MaterialDialog(it).show {
                title(R.string.delete_title)
                message(R.string.delete_message)
                positiveButton(R.string.delete_ok){
                    viewModel.deleteNote(noteUiModel.transNoteView())
                }
                negativeButton(R.string.delete_cancel){
                    showToast(getString(R.string.deleteCancelMsg))
                    dismiss()
                }
                cancelable(false)
            }
        }
    }

    private fun getPreviousFragmentNote(){
        arguments?.let {
            noteUiModel = it[NOTE_DETAIL_BUNDLE_KEY] as NoteUiModel
            defaultMode()
        }
    }

    private fun appBarOffSetChangeSource() = app_bar.offsetChanges()
        .map { offset ->
            if (offset < COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD) TbCollapse
            else TbExpanded
        }
        .subscribe { viewModel.setToolbarState(it) }
        .addCompositeDisposable()

    fun defaultMode(){
        viewModel.setNoteMode(DefaultMode, viewModel.finalNote.value ?: noteUiModel.transNoteView())
    }

    private fun editMode(){
        viewModel.setNoteMode(EditMode, null)
    }

    fun editDoneMode(){
        viewModel.setNoteMode(
            EditDoneMode,
            noteUiModel.copy(
                title = note_title.text.toString(),
                body = note_body.text.toString(),
                updated_at = dateUtil.getCurrentTimestamp()
            ).transNoteView()
        )
    }

    fun navNoteListFragment(){
        view?.clearFocus()
        setFragmentResult(REQUEST_KEY_ON_BACK , bundleOf(NOTE_DETAIL_BUNDLE_KEY to viewModel.finalNote.value?.transNoteUiModel()))
        findNavController().popBackStack()
    }

    fun isEditCancelMenu(): Boolean = toolbar_primary_icon.drawable
        .equalDrawable(R.drawable.ic_cancel_24dp)

    fun isEditDoneMenu(): Boolean = toolbar_secondary_icon.drawable
        .equalDrawable(R.drawable.ic_done_24dp)

}
