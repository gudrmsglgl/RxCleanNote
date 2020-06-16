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
import kotlinx.android.synthetic.main.layout_note_list_item.view.*
import timber.log.Timber

class NoteListAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        Timber.d("Adapter_list size:${list.size}")
        differ.submitList(list)
    }

    class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(item: NoteUiModel) = with(itemView) {

            setOnClickListener {
                Toast.makeText(context, item.title+"normal", Toast.LENGTH_SHORT).show()
            }

            setOnLongClickListener {
                Toast.makeText(context, item.title+"long", Toast.LENGTH_SHORT).show()
                true
            }

            note_title.text = item.title
            note_timestamp.text = item.updated_at

        }

    }
}