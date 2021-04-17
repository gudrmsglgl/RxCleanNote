package com.cleannote.extension.rxbinding

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.recyclerview.RecyclerViewScrollEvent
import com.jakewharton.rxbinding4.view.clicks
import java.util.concurrent.TimeUnit

fun RecyclerViewScrollEvent.lastVisibleItemPos(): Int =
    (this.view.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

fun RecyclerViewScrollEvent.shouldNextPagePos(): Int? =
    this.view.adapter?.itemCount?.minus(4)

fun View.singleClick() =
    clicks().throttleFirst(2000, TimeUnit.MILLISECONDS)
