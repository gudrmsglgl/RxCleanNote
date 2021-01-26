package com.cleannote.notelist.holder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.cleannote.notelist.SubjectManager

abstract class BaseHolder<ITEM>(binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: ITEM, position: Int, subjectManager: SubjectManager)
}