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
import timber.log.Timber

class NoteListAdapter(
    val context: Context,
    val glideReqManager: RequestManager,
    val subjectManager: SubjectManager
): ListAdapter<NoteUiModel, BaseHolder<NoteUiModel>>(NoteDiffCallback) {

    private val _checkedNotes: HashMap<String, NoteUiModel> = hashMapOf()
    val checkedNotes: List<NoteUiModel>
        get() = _checkedNotes.values.toList()

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

    fun fetchRecyclerView(loadNotes: List<NoteUiModel>){
        if (_checkedNotes.isNotEmpty())
            submitList(
                changeLoadNotesToCheckNotes(loadNotes)
            )
        else
            submitList(loadNotes)
    }

    private fun changeLoadNotesToCheckNotes(loadedNotes: List<NoteUiModel>) = loadedNotes
        .toMutableList()
        .apply {
            _checkedNotes.forEach { ( _ , value) ->
                val target = find { it.id == value.id }
                val targetIndex = indexOf(target)
                if (targetIndex != -1){
                    remove(target)
                    add(targetIndex, value)
                }
            }
        }

    fun changeAllNoteMode(noteMode: NoteMode){
        val changedNote = currentList
            .map { it.apply { mode = noteMode } }

        submitList(changedNote)
        notifyDataSetChanged()
    }

    private fun changeNoteMode(item: NoteUiModel, mode: NoteMode){
        currentList.apply {
            val selectItem = find { it.id == item.id }
            selectItem?.mode = mode
        }
    }

    fun isDefaultMode() = currentList.any {
        it.mode == Default
    }

    fun deleteChecked(item: NoteUiModel){
        val index = currentList.indexOf(item)
        changeNoteMode(item, MultiSelect)
        _checkedNotes.put(item.id, item)
        submitList(currentList)
        //notifyItemChanged(index)
    }

    fun deleteNotChecked(item: NoteUiModel){
        val index = currentList.indexOf(item)
        changeNoteMode(item, MultiDefault)
        _checkedNotes.remove(item.id)
        submitList(currentList)
        //notifyItemChanged(index)
    }

    fun deleteCheckClear() = _checkedNotes.clear()

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        subjectManager.releaseSubjects()
    }

}