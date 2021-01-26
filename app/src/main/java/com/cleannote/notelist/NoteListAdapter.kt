package com.cleannote.notelist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.cleannote.app.R
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.cleannote.notelist.holder.BaseHolder
import com.cleannote.notelist.holder.NoteViewHolder

class NoteListAdapter(
    val context: Context,
    val glideReqManager: RequestManager,
    val subjectManager: SubjectManager
): ListAdapter<NoteUiModel, BaseHolder<NoteUiModel>>(NoteDiffCallback) {

    private val _checkedNotes: MutableSet<NoteUiModel> = hashSetOf()
    val checkedNotes: List<NoteUiModel>
        get() = _checkedNotes.toList()

    override fun getItemId(position: Int): Long {
        return currentList[position].hashCode().toLong()
    }

    object NoteDiffCallback: DiffUtil.ItemCallback<NoteUiModel>(){
        override fun areItemsTheSame(oldItem: NoteUiModel, newItem: NoteUiModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NoteUiModel, newItem: NoteUiModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<NoteUiModel>{
        return NoteViewHolder(
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_note_list,
                parent,
                false
            ),
            requestManager = glideReqManager
        )
    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: BaseHolder<NoteUiModel>, position: Int) {
        holder.bind(currentList[position], position, subjectManager)
    }

    fun changeNoteMode(noteMode: NoteMode){
        val changedNote = currentList
            .map { it.apply { mode = noteMode } }

        submitList(changedNote)
        notifyDataSetChanged()
    }

    fun isDefaultMode() = currentList.any {
        it.mode == Default
    }

    fun deleteChecked(position: Int) = _checkedNotes.add(currentList[position])
    fun deleteNotChecked(position: Int) = _checkedNotes.remove(currentList[position])
    fun deleteCheckClear() = _checkedNotes.clear()

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subjectManager.releaseSubjects()
    }

}