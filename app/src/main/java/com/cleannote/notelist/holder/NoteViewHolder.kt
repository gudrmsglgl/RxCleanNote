package com.cleannote.notelist.holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.cleannote.app.databinding.ItemNoteListBinding
import com.cleannote.extension.rxbinding.singleClick
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteMode.SingleDelete
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
    private val swipeCallback: SwipeHelperCallback
): BaseHolder<NoteUiModel>(binding){

    override fun bind(
        item: NoteUiModel,
        position: Int,
        clickSubject: PublishSubject<NoteUiModel>,
        longClickSubject: PublishSubject<Unit>
    ) {
        binding.apply {
            glideReqManager = requestManager
            noteUiModel = item

            /*swipeMenuDelete
                .singleClick()
                .map {
                    item.apply { mode = SingleDelete }
                }
                .subscribe(clickSubject)*/

            swipeMenuDelete.setOnClickListener {
                if (isClamped(this@NoteViewHolder)){
                    swipeCallback.closeDeleteMenu(this@NoteViewHolder)
                }
            }

            with(itemNote){
                when (item.mode){
                    NoteMode.Default -> {
                        clicks()
                            .map { item }
                            .subscribe(clickSubject)

                        longClicks { true }
                            .subscribe (longClickSubject)
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

    private fun isClamped(holder: NoteViewHolder) = holder.itemView.tag as? Boolean ?: false
}