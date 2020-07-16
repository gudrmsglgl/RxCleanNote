package com.cleannote.notedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.cleannote.app.R
import com.cleannote.common.BaseFragment
import com.cleannote.extension.*
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.data.notedetail.NoteTitleState.*
import com.cleannote.presentation.data.notedetail.TextMode.*
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbCollapse
import com.cleannote.presentation.data.notedetail.DetailToolbarState.TbExpanded
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.jakewharton.rxbinding4.material.offsetChanges
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import com.yydcdut.markdown.MarkdownProcessor
import com.yydcdut.markdown.syntax.edit.EditFactory
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.layout_note_detail_toolbar.*



const val NOTE_DETAIL_BUNDLE_KEY = "com.cleannote.notedetail.select_note"

@AndroidEntryPoint
class NoteDetailFragment: BaseFragment(R.layout.fragment_note_detail) {

    private val COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD = -75

    lateinit var noteUiModel: NoteUiModel
    private val viewModel: NoteDetailViewModel by viewModels()

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
        .subscribe { isEditCancelMenu ->
            if (isEditCancelMenu) {
                // edit Menu cancel
                note_title.takeIf { it.isFocused }?.clearFocus()
                note_body.takeIf { it.isFocused }?.clearFocus()
                viewModel.setNoteMode(DefaultMode)
            }
            else {
                // default Menu back
                //TODO:: Note update & hidekeyboard
                timber("d","NoteDetailFragment_Before onBack: $noteUiModel")
                noteUiModel.body = "afterOnBack"
                setFragmentResult("requestOnBack",
                    bundleOf(NOTE_DETAIL_BUNDLE_KEY to noteUiModel)
                )

                findNavController().popBackStack()
            }
        }.addCompositeDisposable()

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

    private fun setToolbarTitle(){
        viewModel.takeIf { it.isEditMode() }?.setNoteMode(DefaultMode)
        tool_bar_title.text = viewModel.getNoteTile()
    }

    private fun getPreviousFragmentNote(){
        arguments?.let {
            noteUiModel = it[NOTE_DETAIL_BUNDLE_KEY] as NoteUiModel
            viewModel.setNoteTitle(noteUiModel.title)
            viewModel.setNoteBody(noteUiModel.body)
            note_body.setText(noteUiModel.body)
        }
    }

    private fun observeAppBarChange() = app_bar.offsetChanges()
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
                .setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_cancel_24dp))
            toolbar_secondary_icon
                .setImageDrawable(ContextCompat.getDrawable(it,R.drawable.ic_done_24dp))
        }
    }

    private fun toolbarDefaultMenu(){
        activity?.let {
            toolbar_primary_icon
                .setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_arrow_back_24dp))

            toolbar_secondary_icon
                .setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_delete_24dp))
        }
    }

    private fun isEditPrimaryMenu() = toolbar_primary_icon.drawable.constantState ==
            resources.getDrawable(R.drawable.ic_cancel_24dp,null).constantState

    private fun isEditSecondaryMenu() = toolbar_secondary_icon.drawable.constantState ==
            resources.getDrawable(R.drawable.ic_done_24dp,null).constantState
}
