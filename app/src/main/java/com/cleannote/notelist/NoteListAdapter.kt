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
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.holder.BaseHolder
import com.cleannote.notelist.holder.NoteViewHolder
import com.cleannote.notelist.holder.SingleDeleteHolder
import com.cleannote.presentation.notelist.NoteListViewModel
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber

class NoteListAdapter(
    val context: Context,
    val glideReqManager: RequestManager,
    val noteListViewModel: NoteListViewModel
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val DEFAULT_ITEM = 1
        private const val SINGLE_DELETE_ITEM = 2
        private const val MULTI_DELETE_SELECT_ITEM = 3
    }

    override fun getItemViewType(position: Int): Int =
        when(differ.currentList[position].mode){
            Default -> DEFAULT_ITEM
            SingleDelete -> SINGLE_DELETE_ITEM
            else -> MULTI_DELETE_SELECT_ITEM
        }

    private val _clickNoteSubject: PublishSubject<NoteUiModel> = PublishSubject.create()
    val clickNoteSubject: PublishSubject<NoteUiModel>
        get() = _clickNoteSubject

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NoteUiModel>(){
        override fun areItemsTheSame(oldItem: NoteUiModel, newItem: NoteUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteUiModel, newItem: NoteUiModel): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
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

    override fun getItemCount(): Int = differ.currentList.size

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseHolder<NoteUiModel>).bind(differ.currentList[position], position, _clickNoteSubject)
    }

    fun submitList(list: List<NoteUiModel>) {
        Timber.tag("RxCleanNote").d("Adapter_submitList size:${list.size}")
        differ.submitList(list)
    }

    fun transNoteSingleDelete(position: Int){
        with (differ) {
            currentList.apply {
                forEachIndexed { index, note->
                    if (index == position)
                        note.apply { mode = SingleDelete }
                    else
                        note.apply { mode = Default}
                }
            }
            submitList(currentList)
        }
        notifyItemChanged(position)
    }

    fun isNotDefaultNote() = differ.currentList.any {
        it.mode != Default
    }

    fun transAllDefaultNote(){
        with(differ){
            currentList.apply {
                forEach { it.apply { mode = Default } }
            }
            submitList(currentList)
        }
        notifyDataSetChanged()
    }

    fun transAllMultiSelectDefaultNote(){
        with(differ){
            currentList.apply {
                forEach { it.apply { mode = MultiDefault } }
            }
            submitList(currentList)
        }
        notifyDataSetChanged()
    }

    fun getMultiSelectedNotes(): List<NoteUiModel> =  differ.currentList.filter { it.mode == MultiSelected }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _clickNoteSubject.onComplete()
    }

    fun setMultiSelectCheck(position: Int, binding: ItemNoteListBinding) {
        if (binding.checkboxDelete.isChecked){
            binding.checkboxDelete.isChecked = false
            differ.currentList[position].apply { mode = MultiDefault }
        }
        else {
            binding.checkboxDelete.isChecked = true
            differ.currentList[position].apply { mode = MultiSelected }
        }
    }

}