package com.cleannote.notelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cleannote.app.R
import com.cleannote.model.NoteUiModel
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.layout_note_list_item.view.*
import timber.log.Timber

class NoteListAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val _clickNoteSubject: PublishSubject<NoteUiModel> = PublishSubject.create()
    val clickNoteSubject: PublishSubject<NoteUiModel>
        get() = _clickNoteSubject

    private val _longClickNoteSubject: PublishSubject<NoteUiModel> = PublishSubject.create()
    val longClickNoteSubject: PublishSubject<NoteUiModel>
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_note_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       when (holder) {
           is NoteViewHolder -> {
               Timber.d("Adapter_onBindViewHolder pos:${position}")
               holder.bind(differ.currentList[position])
           }
       }
    }


    fun submitList(list: List<NoteUiModel>) {
        Timber.tag("RxCleanNote").d("Adapter_submitList size:${list.size}")
        differ.submitList(list)
    }

    inner class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(item: NoteUiModel) = with(itemView) {

            clicks()
                .map { item }
                .subscribe(_clickNoteSubject)

            longClicks { true }
                .map { item }
                .subscribe(_longClickNoteSubject)

            setOnLongClickListener {
                Toast.makeText(context, item.title+"long", Toast.LENGTH_SHORT).show()
                true
            }

            note_title.text = item.title
            note_timestamp.text = item.updated_at

        }

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _clickNoteSubject.onComplete()
        _longClickNoteSubject.onComplete()
    }
}