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
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.data.NoteTitleState.*
import com.cleannote.presentation.data.TextMode.*
import com.cleannote.presentation.data.ToolbarState.TbCollapse
import com.cleannote.presentation.data.ToolbarState.TbExpanded
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.focusChanges
import com.jakewharton.rxbinding4.widget.textChangeEvents
import com.jakewharton.rxbinding4.widget.textChanges
import com.yydcdut.markdown.MarkdownProcessor
import com.yydcdut.markdown.syntax.edit.EditFactory
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.layout_note_detail_toolbar.*

/**
 * A simple [Fragment] subclass.
 */
const val NOTE_DETAIL_BUNDLE_KEY = "com.cleannote.notedetail.select_note"

class NoteDetailFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseFragment(R.layout.fragment_note_detail) {

    private val COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD = -75

    private val viewModel: NoteDetailViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMarkdown()
        getPreviousFragmentNote()

        observeAppBarChange()
        subscribeToolbarState()
        subscribeNoteTitleState()

        observeTitleChange()
        observeBodyChange()
        subscribeNoteMode()
        observeFirstOptionMenu()
    }

    private fun observeTitleChange() = Observable.combineLatest(
        note_title
            .textChanges()
            .filter { note_title.isFocused }
            .doOnNext { viewModel.setNoteMode(EditMode) },
        toolbar_secondary_icon
            .clicks()
            .filter { isEditSecondaryMenu() }
            .doOnNext { note_title.clearFocus(); viewModel.setNoteMode(DefaultMode) },
        BiFunction { text: CharSequence, _:Unit ->
            text.toString()
        })
        .subscribe { viewModel.setNoteTitle(it) }
        .addCompositeDisposable()

    private fun observeBodyChange() = Observable.combineLatest(
        note_body
            .textChanges()
            .filter { note_body.isFocused }
            .doOnNext { viewModel.setNoteMode(EditMode) },
        toolbar_secondary_icon
            .clicks()
            .filter { isEditSecondaryMenu() }
            .doOnNext { note_body.clearFocus(); viewModel.setNoteMode(DefaultMode) },
        BiFunction { text: CharSequence, _:Unit ->
            text.toString()
        })
        .subscribe { viewModel.setNoteBody(it) }
        .addCompositeDisposable()

    private fun subscribeNoteMode() = viewModel.noteMode
        .observe( viewLifecycleOwner, Observer {  mode ->
            when (mode) {
                is EditMode -> {
                    toolbarEditMenu()
                }
                is DefaultMode -> {
                    toolbarDefaultMenu()
                    view?.hideKeyboard()
                }
            }
        })

    private fun observeFirstOptionMenu() = toolbar_primary_icon.singleClick()
        .map { isEditPrimaryMenu() }
        .subscribe { editMenu ->
            if (editMenu) {
                // edit Menu cancel
                note_title.clearFocus()
                viewModel.setNoteMode(DefaultMode)
            }
            else {
                // default Menu back
                //TODO:: Note update & hidekeyboard
                findNavController().popBackStack()
            }
        }.addCompositeDisposable()

    private fun subscribeNoteTitleState() = viewModel.noteTitleState.observe( viewLifecycleOwner,
        Observer { titleState ->
            when (titleState) {
                is NtExpanded -> {
                    note_title.setText(viewModel.getNoteTile())
                    note_title.setSelection(viewModel.getNoteTile().length)
                }
                is NtCollapse -> {
                    viewModel.takeIf { it.isEditMode() }?.setNoteMode(DefaultMode)
                    tool_bar_title.text = viewModel.getNoteTile()
                }
            }
        })

    private fun getPreviousFragmentNote(){
        arguments?.let {
            val note = it[NOTE_DETAIL_BUNDLE_KEY] as NoteUiModel
            viewModel.setNoteTitle(note.title)
        }
    }

    private fun observeAppBarChange() = app_bar.offsetChanges()
        .map { offset ->
            if (offset < COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD) TbCollapse
            else TbExpanded
        }
        .subscribe { viewModel.setToolbarState(it) }
        .addCompositeDisposable()

    private fun subscribeToolbarState() = viewModel.toolbarState
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

    private fun setupMarkdown(){
        activity?.run {
            val markdownProcessor = MarkdownProcessor(this)
            markdownProcessor.factory(EditFactory.create())
            markdownProcessor.live(note_body)
        }
    }

    private fun toolbarEditMenu(){
        activity?.let {
            toolbar_primary_icon
                .setImageDrawable(resources.getDrawable(R.drawable.ic_cancel_24dp))
            toolbar_secondary_icon
                .setImageDrawable(resources.getDrawable(R.drawable.ic_done_24dp))
        }
    }

    private fun toolbarDefaultMenu(){
        activity?.let {
            toolbar_primary_icon
                .setImageDrawable(resources.getDrawable(R.drawable.ic_arrow_back_24dp))

            toolbar_secondary_icon
                .setImageDrawable(resources.getDrawable(R.drawable.ic_delete_24dp))
        }
    }

    private fun isEditPrimaryMenu() = toolbar_primary_icon.drawable.constantState ==
            resources.getDrawable(R.drawable.ic_cancel_24dp).constantState

    private fun isEditSecondaryMenu() = toolbar_secondary_icon.drawable.constantState ==
            resources.getDrawable(R.drawable.ic_done_24dp).constantState
}
