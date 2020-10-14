package com.cleannote.notelist.holder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import io.reactivex.rxjava3.subjects.PublishSubject

abstract class BaseHolder<ITEM>(binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: ITEM, position: Int, clickSubject: PublishSubject<ITEM>)
}