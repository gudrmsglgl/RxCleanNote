package com.cleannote.notelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.*
import com.cleannote.app.R
import com.cleannote.model.NoteMode
import com.cleannote.model.NoteMode.*
import com.cleannote.model.NoteUiModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_note_list.view.*
import timber.log.Timber

class NoteListAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val DEFAULT_ITEM = 1
        private const val SINGLE_DELETE_ITEM = 2
        private const val MULTI_DELETE_DEFAULT_ITEM = 3
        private const val MULTI_DELETE_SELECT_ITEM = 4
    }

    override fun getItemViewType(position: Int): Int =
        //return if (differ.currentList[position].isSingleDeleteMode) SINGLE_DELETE_ITEM else DEFAULT_ITEM
        when(differ.currentList[position].mode){
            Default -> DEFAULT_ITEM
            SingleDelete -> SINGLE_DELETE_ITEM
            MultiDefault -> MULTI_DELETE_DEFAULT_ITEM
            else -> MULTI_DELETE_SELECT_ITEM
        }


    private val _clickNoteSubject: PublishSubject<NoteUiModel> = PublishSubject.create()
    val clickNoteSubject: PublishSubject<NoteUiModel>
        get() = _clickNoteSubject

    private val _longClickNoteSubject: PublishSubject<Int> = PublishSubject.create()
    val longClickNoteSubject: PublishSubject<Int>
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

            DEFAULT_ITEM ->  NoteViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_note_list,
                    parent,
                    false
                )
            )

            else -> NoteMenuHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_single_delete_note,
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

    fun transItemMenu(position: Int){
        val transNotes = differ.currentList.apply {
            forEachIndexed { index, _ ->
                if (index == position)
                    get(index).apply { isSingleDeleteMode = true }
                else
                    get(index).apply { isSingleDeleteMode = false }
            }
        }
        differ.submitList(transNotes)
        notifyDataSetChanged()
    }

    fun isShowMenu(): Boolean  = differ.currentList.any {
        it.isSingleDeleteMode
    }

    fun hideMenu() = differ.currentList.apply {
        forEachIndexed { index, _ ->
            get(index).apply { isSingleDeleteMode = false }
        }
    }.run {
        differ.submitList(this)
        notifyDataSetChanged()
    }

    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(item: NoteUiModel, position: Int) = with(itemView) {

            clicks()
                .map { item }
                .subscribe(_clickNoteSubject)

            longClicks { true }
                .map { position }
                .subscribe(_longClickNoteSubject)

            note_title.text = item.title
            note_timestamp.text = item.updated_at

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