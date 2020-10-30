package com.cleannote.notelist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.app.databinding.ItemNoteListBinding
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.holder.BaseHolder
import com.cleannote.notelist.holder.NoteViewHolder
import com.cleannote.notelist.holder.SingleDeleteHolder
import com.cleannote.presentation.notelist.NoteListViewModel
import com.jakewharton.rxbinding4.recyclerview.dataChanges
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.checkedChanges
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber

class NoteListAdapter(
    val context: Context,
    val glideReqManager: RequestManager,
    val noteListViewModel: NoteListViewModel
): ListAdapter<NoteUiModel, BaseHolder<NoteUiModel>>(NoteDiffCallback) {

    companion object{
        private const val DEFAULT_ITEM = 1
        private const val SINGLE_DELETE_ITEM = 2
        private const val MULTI_DELETE_SELECT_ITEM = 3
    }

    override fun getItemViewType(position: Int): Int =
        when(currentList[position].mode){
            Default -> DEFAULT_ITEM
            SingleDelete -> SINGLE_DELETE_ITEM
            else -> MULTI_DELETE_SELECT_ITEM
        }

    override fun getItemId(position: Int): Long {
        return currentList[position].hashCode().toLong()
    }
    private val _clickNoteSubject: PublishSubject<NoteUiModel> = PublishSubject.create()
    val clickNoteSubject: PublishSubject<NoteUiModel>
        get() = _clickNoteSubject

    object NoteDiffCallback: DiffUtil.ItemCallback<NoteUiModel>(){
        override fun areItemsTheSame(oldItem: NoteUiModel, newItem: NoteUiModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NoteUiModel, newItem: NoteUiModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<NoteUiModel> =
        when (viewType){

            SINGLE_DELETE_ITEM -> SingleDeleteHolder(binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_single_delete_note,
                parent,
                false)
            )

            else -> NoteViewHolder(
                binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_note_list,
                    parent,
                    false
                ),
                requestManager = glideReqManager,
                viewModel = noteListViewModel
            )
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: BaseHolder<NoteUiModel>, position: Int) {
        holder.bind(currentList[position], position, _clickNoteSubject)
    }

    fun changeNoteMode(noteMode: NoteMode, position: Int? = null){
        val changedNote = if (noteMode == SingleDelete){
            currentList.apply {
                find { it.mode == SingleDelete }?.let { it.mode = Default }
                get(position!!).mode = SingleDelete
            }
        }
        else {
            currentList.map { it.apply { mode = noteMode } }
        }
        submitList(changedNote)
        notifyDataSetChanged()
    }

    fun isNotDefaultNote() = currentList.any {
        it.mode == MultiDefault || it.mode == SingleDelete
    }

    fun isSwipeMode() = currentList.any {
        it.mode == Default || it.mode == SingleDelete
    }

    fun getMultiSelectedNotes(): List<NoteUiModel> =  currentList.filter { it.mode == MultiSelected }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _clickNoteSubject.onComplete()
    }

    fun setMultiSelectCheck(position: Int, binding: ItemNoteListBinding) {
        if (binding.checkboxDelete.isChecked){
            binding.checkboxDelete.isChecked = false
            currentList[position].apply { mode = MultiDefault }
        }
        else {
            binding.checkboxDelete.isChecked = true
            currentList[position].apply { mode = MultiSelected }
        }
    }

}