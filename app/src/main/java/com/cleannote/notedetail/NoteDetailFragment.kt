package com.cleannote.notedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.cleannote.app.R
import com.cleannote.common.BaseFragment
import com.cleannote.extension.*
import com.cleannote.mapper.NoteMapper
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.data.notedetail.NoteTitleState.*
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbCollapse
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbExpanded
import com.cleannote.presentation.data.notedetail.TextMode
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.layout_note_detail_toolbar.*

/**
 * A simple [Fragment] subclass.
 */
const val NOTE_DETAIL_BUNDLE_KEY = "com.cleannote.notedetail.select_note"

class NoteDetailFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val noteMapper: NoteMapper
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

        subscribeToolbarState()
        subscribeNoteTitleState()

        subscribeNoteMode()
    }

    private fun noteTitleChangeSource() = note_title
        .textChanges()
        .filter { note_title.isFocused }
        .subscribe { _ ->
            viewModel.setNoteMode(EditMode)
        }
        .addCompositeDisposable()

    private fun noteBodyChangeSource() = note_body
        .textChanges()
        .filter { note_body.isFocused }
        .subscribe { viewModel.setNoteMode(EditMode) }
        .addCompositeDisposable()

    private fun subscribeNoteMode() = viewModel.noteMode
        .observe( viewLifecycleOwner, Observer {  mode ->
            when (mode) {
                is InitMode -> {
                    fetchNoteUi()
                }
                is EditMode -> {
                    toolbarEditMenu()
                }
                is EditDoneMode -> {
                    releaseFocus()
                    fetchNoteUi()
                    toolbarDefaultMenu()
                    view?.hideKeyboard()
                }
            }
        })

    private fun menuPrimarySource() = toolbar_primary_icon.clicks()
        .map { isEditCancelMenu() }
        .doOnNext { cancelMenu ->
            if (cancelMenu) viewModel.setNoteMode(EditDoneMode)
            else findNavController().popBackStack()
        }
        .subscribe {
            releaseFocus()
        }
        .addCompositeDisposable()

    private fun menuSecondarySource() = toolbar_secondary_icon.clicks()
        .map { isEditDoneMenu() }
        .subscribe { doneMenu ->
            if (doneMenu) setNote(EditDoneMode)
            else deleteNote()
        }
        .addCompositeDisposable()

    private fun setNote(mode: TextMode) = with(viewModel){
        setNote(noteMapper.mapToView(
            noteUiModel.apply {
                title = note_title.text.toString()
                body = note_body.text.toString()
            }
        ))
        setNoteMode(mode)
    }

    private fun deleteNote() = with(viewModel) {
        deleteNote(noteMapper.mapToView(noteUiModel))
    }

    private fun fetchNoteUi(){
        setNoteTitle()
        setNoteBody()
    }

    private fun subscribeNoteTitleState() = viewModel.noteTitleState.observe( viewLifecycleOwner,
        Observer { titleState ->
            when (titleState) {
                is NtExpanded -> setNoteTitle()
                is NtCollapse -> setToolbarTitle()
            }
        })

    private fun setNoteTitle() = with(note_title){
        setText(viewModel.getNoteTile())
        setSelection(viewModel.getNoteTile().length)
    }

    private fun setNoteBody() = with(note_body){
        setText(viewModel.getNoteBody())
        setSelection(viewModel.getNoteBody().length)
    }

    private fun setToolbarTitle(){
        viewModel.takeIf { it.isEditMode() }?.setNoteMode(EditDoneMode)
        tool_bar_title.text = viewModel.getNoteTile()
    }

    private fun getPreviousFragmentNote(){
        arguments?.let {
            noteUiModel = it[NOTE_DETAIL_BUNDLE_KEY] as NoteUiModel
            setNote(InitMode)
        }
    }

    private fun appBarOffSetChangeSource() = app_bar.offsetChanges()
        .map { offset ->
            if (offset < COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD) TbCollapse
            else TbExpanded
        }
        .subscribe { viewModel.setToolbarState(it) }
        .addCompositeDisposable()

    private fun subscribeToolbarState() = viewModel.detailToolbarState
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

    private fun isEditCancelMenu(): Boolean = toolbar_primary_icon.drawable
        .equalDrawable(R.drawable.ic_cancel_24dp)

    private fun isEditDoneMenu(): Boolean = toolbar_secondary_icon.drawable
        .equalDrawable(R.drawable.ic_done_24dp)

    private fun releaseFocus(){
        note_title.takeIf { it.isFocused }?.clearFocus()
        note_body.takeIf { it.isFocused }?.clearFocus()
    }
}
