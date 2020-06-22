package com.cleannote.notedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.cleannote.app.R
import com.cleannote.common.BaseFragment
import com.cleannote.model.NoteUiModel
import com.cleannote.presentation.data.NoteTitleState.*
import com.cleannote.presentation.data.ToolbarState.TbCollapse
import com.cleannote.presentation.data.ToolbarState.TbExpanded
import com.cleannote.presentation.notedetail.NoteDetailViewModel
import com.jakewharton.rxbinding4.material.offsetChanges
import com.yydcdut.markdown.MarkdownProcessor
import com.yydcdut.markdown.syntax.edit.EditFactory
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
        toolbarDefaultMenu()
        getPreviousFragmentNote()
        observeAppBarChange()
        subscribeNoteTitleState()
        subscribeToolbarState()
    }

    private fun subscribeNoteTitleState() = viewModel.noteTitleState.observe( viewLifecycleOwner,
        Observer { titleState ->
            when (titleState) {
                is NtExpanded -> note_title.setText(viewModel.getNoteTile())
                is NtCollapse -> tool_bar_title.text = viewModel.getNoteTile()
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
                is TbCollapse -> note_title.setText("")
                is TbExpanded -> tool_bar_title.text = ""
            }
        })

    private fun setupMarkdown(){
        activity?.run {
            val markdownProcessor = MarkdownProcessor(this)
            markdownProcessor.factory(EditFactory.create())
            markdownProcessor.live(note_body)
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
}
