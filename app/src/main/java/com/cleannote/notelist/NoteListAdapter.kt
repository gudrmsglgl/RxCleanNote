package com.cleannote.notelist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.cleannote.app.R
import com.cleannote.extension.gone
import com.cleannote.extension.visible
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_note_list.view.*
import timber.log.Timber

class NoteListAdapter(
    val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val DEFAULT_ITEM = 1
        private const val SINGLE_DELETE_ITEM = 2
        private const val MULTI_DELETE_SELECT_ITEM = 3
    }

    override fun getItemViewType(position: Int): Int =
        //return if (differ.currentList[position].isSingleDeleteMode) SINGLE_DELETE_ITEM else DEFAULT_ITEM
        when(differ.currentList[position].mode){
            Default -> DEFAULT_ITEM
            SingleDelete -> SINGLE_DELETE_ITEM
            else -> MULTI_DELETE_SELECT_ITEM
        }


    private val _clickNoteSubject: PublishSubject<NoteUiModel> = PublishSubject.create()
    val clickNoteSubject: PublishSubject<NoteUiModel>
        get() = _clickNoteSubject

    private val _longClickNoteSubject: PublishSubject<Unit> = PublishSubject.create()
    val longClickNoteSubject: PublishSubject<Unit>
        get() = _longClickNoteSubject

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

            SINGLE_DELETE_ITEM -> NoteMenuHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_single_delete_note,
                    parent,
                    false
                )
            )

            else -> NoteViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_note_list,
                    parent,
                    false
                )
            )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when (holder) {
           is NoteViewHolder -> holder.bind(differ.currentList[position], position)

           is NoteMenuHolder -> holder.bind(differ.currentList[position])
       }
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
        notifyDataSetChanged()
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

    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(item: NoteUiModel, position: Int) = when(item.mode) {
            Default -> uiHolderDefault(item)
            else -> uiHolderMultiDeleteSelect(item, position)
        }

        private fun uiHolderDefault(item: NoteUiModel){
            itemView.apply {
                margin_view.visible()
                select_delete.gone()

                note_title.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.note_title_color))
                    text = item.title
                }
                note_timestamp.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.default_grey))
                    text = item.updated_at
                }

                clicks()
                    .map { item }
                    .subscribe(_clickNoteSubject)

                longClicks { true }
                    .subscribe(_longClickNoteSubject)
            }
        }

        private fun uiHolderMultiDeleteSelect(item: NoteUiModel, position: Int){
            itemView.apply {
                margin_view.gone()
                select_delete.visible()
                select_delete.isChecked = item.mode == MultiSelected
                note_title.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.multi_delete_default_color))
                    text = item.title
                }
                note_timestamp.apply {
                    setTextColor(ContextCompat.getColor(context, R.color.multi_delete_default_color))
                    text = item.updated_at
                }

                clicks()
                    .subscribe {
                        if (select_delete.isChecked){
                            select_delete.isChecked = false
                            differ.currentList[position].apply { mode = MultiDefault }
                        }
                        else {
                            select_delete.isChecked = true
                            differ.currentList[position].apply { mode = MultiSelected }
                        }
                    }
            }
        }
    }

    inner class NoteMenuHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: NoteUiModel) = with (itemView) {

            findViewById<LinearLayout>(R.id.menu_delete_container)
                .apply {
                    clicks()
                        .map { item }
                        .subscribe(_clickNoteSubject)
                }

        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _clickNoteSubject.onComplete()
        _longClickNoteSubject.onComplete()
    }

}