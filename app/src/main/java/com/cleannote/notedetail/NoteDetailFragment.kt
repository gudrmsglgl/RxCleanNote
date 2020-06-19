package com.cleannote.notedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.cleannote.app.R
import com.cleannote.common.BaseFragment
import com.cleannote.model.NoteUiModel

/**
 * A simple [Fragment] subclass.
 */
const val NOTE_DETAIL_BUNDLE_KEY = "com.cleannote.notedetail.select_note"

class NoteDetailFragment constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseFragment(R.layout.fragment_note_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPreviousFragmentNote()
    }

    private fun getPreviousFragmentNote(){
        arguments?.let {
            val note = it[NOTE_DETAIL_BUNDLE_KEY] as NoteUiModel
            timber("d", "selected note id: ${note.id}, title: ${note.title}")
        }
    }
}
