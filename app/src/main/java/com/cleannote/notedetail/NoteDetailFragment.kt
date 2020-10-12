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
import com.cleannote.common.BaseFragment
import com.cleannote.common.DateUtil
import com.cleannote.extension.*
import com.cleannote.mapper.NoteMapper
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.data.State.ERROR
import com.cleannote.presentation.data.State.SUCCESS
import com.cleannote.presentation.data.notedetail.NoteTitleState.*
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbCollapse
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbExpanded
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.layout_note_detail_toolbar.*

const val NOTE_DETAIL_BUNDLE_KEY = "com.cleannote.notedetail.select_note"
const val NOTE_DETAIL_DELETE_KEY = "com.cleannote.notedetail.request_onback_delete"
const val REQUEST_KEY_ON_BACK = "com.cleannote.notedetail.request_onback"

class NoteDetailFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val dateUtil: DateUtil
) : BaseFragment(R.layout.fragment_note_detail) {

    private val COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD = -75

    lateinit var noteUiModel: NoteUiModel
    private val viewModel: NoteDetailViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPreviousFragmentNote()

        appBarOffSetChangeSource()
        noteTitleChangeSource()
        noteBodyChangeSource()

        menuPrimarySource()
        menuSecondarySource()

        toolbarCollapseExpandedAnimation()
        setTitleOfCollapseExpanded()
        noteModeChangeToolbarUi()

        subscribeUpdateNote()
        subscribeDeleteNote()
    }

    private fun subscribeUpdateNote() = viewModel.updatedNote
        .observe(viewLifecycleOwner, Observer {
            if (it != null){
                when (it.status) {
                    is SUCCESS -> {
                        noteUiModel = it.data!!.transNoteUiModel()
                        showToast(getString(R.string.updateSuccessMsg))
                        fetchNoteUi(noteUiModel)
                    }
                    is ERROR -> {
                        fetchNoteUi(noteUiModel)
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
                        setFragmentResult(REQUEST_KEY_ON_BACK, bundleOf(NOTE_DETAIL_DELETE_KEY to noteUiModel))
                        findNavController().popBackStack()
                    }
                    is ERROR -> {
                        it.sendFirebaseThrowable()
                        showToast(getString(R.string.deleteErrorMsg))
                    }
                    else -> {}
                }
            }
        })

    private fun noteTitleChangeSource() = note_title
        .textChanges()
        .filter { note_title.isFocused }
        .subscribe { editMode() }
        .addCompositeDisposable()

    private fun noteBodyChangeSource() = note_body
        .textChanges()
        .filter { note_body.isFocused }
        .subscribe { editMode() }
        .addCompositeDisposable()

    private fun noteModeChangeToolbarUi() = viewModel.noteMode
        .observe( viewLifecycleOwner, Observer {  mode ->
            when (mode) {
                is EditMode -> {
                    toolbarEditMenu()
                }
                else -> {
                    releaseFocus()
                    toolbarDefaultMenu()
                    view?.hideKeyboard()
                }
            }
        })

    private fun menuPrimarySource() = toolbar_primary_icon.clicks()
        .map { isEditCancelMenu() }
        .subscribe { cancelMenu ->
            if (cancelMenu) defaultMode()
            else {
                setFragmentResult(REQUEST_KEY_ON_BACK , bundleOf(NOTE_DETAIL_BUNDLE_KEY to noteUiModel))
                findNavController().popBackStack()
            }
            releaseFocus()
        }
        .addCompositeDisposable()

    private fun menuSecondarySource() = toolbar_secondary_icon.clicks()
        .map { isEditDoneMenu() }
        .subscribe { doneMenu ->
            if (doneMenu) editDoneMode()
            else showDeleteDialog(noteUiModel)
        }
        .addCompositeDisposable()

    private fun showDeleteDialog(deleteMemo: NoteUiModel) = activity?.let {
        MaterialDialog(it).show {
            title(R.string.delete_title)
            message(R.string.delete_message)
            positiveButton(R.string.delete_ok){
                viewModel.deleteNote(deleteMemo.transNoteView())
            }
            negativeButton(R.string.delete_cancel){
                showToast(getString(R.string.deleteCancelMsg))
                dismiss()
            }
            cancelable(false)
        }
    }

    private fun fetchNoteUi(noteUiModel: NoteUiModel){
        setNoteTitle(noteUiModel.title)
        setNoteBody(noteUiModel.body)
    }

    private fun setTitleOfCollapseExpanded() = viewModel.noteTitleState.observe( viewLifecycleOwner,
        Observer { titleState ->
            when (titleState) {
                is NtExpanded -> setNoteTitle(noteUiModel.title)
                is NtCollapse -> setToolbarTitle()
            }
        })

    private fun setNoteTitle(title: String) = with(note_title){
        setText(title)
        setSelection(title.length)
    }

    private fun setNoteBody(body: String) = with(note_body){
        setText(body)
        setSelection(body.length)
    }

    private fun setToolbarTitle(){
        viewModel.takeIf { it.isEditMode() }?.setNoteMode(DefaultMode)
        tool_bar_title.text = noteUiModel.title
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

    private fun toolbarCollapseExpandedAnimation() = viewModel.detailToolbarState
        .observe( viewLifecycleOwner, Observer { toolbarState ->
            when (toolbarState){
                is TbCollapse -> {
                    tool_bar_title.fadeIn()
                    note_title.fadeOut()
                }
                is TbExpanded -> {
                    tool_bar_title.fadeOut()
                    note_title.fadeIn()
                }
            }
        })

    private fun toolbarEditMenu(){
        activity?.let {
            toolbar_primary_icon
                .setImageDrawable(resources.getDrawable(R.drawable.ic_cancel_24dp,null))
            toolbar_secondary_icon
                .setImageDrawable(resources.getDrawable(R.drawable.ic_done_24dp,null))
        }
    }

    private fun toolbarDefaultMenu(){
        activity?.let {
            toolbar_primary_icon
                .setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_back_24dp,null))

            toolbar_secondary_icon
                .setImageDrawable(resources.getDrawable(R.drawable.ic_delete_24dp,null))
        }
    }

    private fun defaultMode(){
        viewModel.setNoteMode(DefaultMode)
        fetchNoteUi(noteUiModel)
    }

    private fun editMode(){
        viewModel.setNoteMode(EditMode)
    }

    private fun editDoneMode(){
        viewModel.setNote(editDoneNoteParam())
    }

    private fun editDoneNoteParam() =
        noteUiModel
            .copy()
            .apply {
                title = note_title.text.toString()
                body = note_body.text.toString()
                updated_at = dateUtil.getCurrentTimestamp()}
            .transNoteView() to EditDoneMode

    private fun isEditCancelMenu(): Boolean = toolbar_primary_icon.drawable
        .equalDrawable(R.drawable.ic_cancel_24dp)

    private fun isEditDoneMenu(): Boolean = toolbar_secondary_icon.drawable
        .equalDrawable(R.drawable.ic_done_24dp)

    private fun releaseFocus(){
        note_title.takeIf { it.isFocused }?.clearFocus()
        note_body.takeIf { it.isFocused }?.clearFocus()
    }
}
