package com.cleannote.notelist.holder

import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemNoteListBinding
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.NoteListAdapter
import com.cleannote.notelist.SwipeHelperCallback
import com.cleannote.presentation.data.notelist.ListToolbarState
import com.cleannote.presentation.notelist.NoteListViewModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber

class NoteViewHolder(
    val binding: ItemNoteListBinding,
    private val requestManager: RequestManager,
    val viewModel: NoteListViewModel,
    private val swipeCallback: SwipeHelperCallback
): BaseHolder<NoteUiModel>(binding){
    override fun bind(item: NoteUiModel, position: Int, clickSubject: PublishSubject<NoteUiModel>) {
        binding.apply {
            glideReqManager = requestManager
            noteUiModel = item

            swipeMenuDelete.setOnClickListener {
                Timber.tag("RxCleanNote").d("swipeMenuDelete Click Clamped: ${this@NoteViewHolder.itemView.tag as? Boolean ?: false}")
                swipeCallback.cancelDeleteMenu(this@NoteViewHolder)
            }

            with(itemNote){
                when (item.mode){
                    NoteMode.Default -> {
                        clicks()
                            .map { item }
                            .subscribe( clickSubject )

                        longClicks { true }
                            .subscribe {
                                viewModel.setToolbarState(ListToolbarState.MultiSelectState)
                                (bindingAdapter as NoteListAdapter).changeNoteMode(NoteMode.MultiDefault)
                            }
                    }
                    else -> {
                        clicks()
                            .subscribe {
                                (bindingAdapter as NoteListAdapter).setMultiSelectCheck(position, binding)
                            }
                    }
                }
            }
        }
    }
}