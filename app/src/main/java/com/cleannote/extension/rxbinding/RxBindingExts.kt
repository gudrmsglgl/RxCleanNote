package com.cleannote.extension.rxbinding

import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.recyclerview.RecyclerViewScrollEvent

fun RecyclerViewScrollEvent.lastVisibleItemPos(): Int =
    (this.view.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

fun RecyclerViewScrollEvent.itemCount(): Int? =
    this.view.adapter?.itemCount?.minus(1)