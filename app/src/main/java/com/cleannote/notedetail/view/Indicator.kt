package com.cleannote.notedetail.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat

class Indicator: LinearLayoutCompat {

    private var mContext: Context? = null
    private var mDefaultRes: Int = 0
    private var mSelectRes: Int = 0

    private val lineView: MutableList<TextView> = mutableListOf()

    constructor(context: Context?): super(context){
        mContext = context
    }

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs){
        mContext = context
    }

    fun create(size: Int?, defaultRes: Int, selectRes: Int, selectPosition: Int){
        this.removeAllViews()
        if (size == null || size == 1) return

        mDefaultRes = defaultRes
        mSelectRes = selectRes

        initLineView(size)

        selectIndicator(selectPosition)
    }

    private fun initLineView(size: Int){
        for (i in 0 until size){
            lineView.add(createLine(size))
            this.addView(lineView[i])
        }
    }

    private fun createLine(size: Int) = TextView(mContext)
        .apply {
            layoutParams = LayoutParams(relativeSizeToWidth(size), 10)
            setPadding(0,0,0,0)
        }

    private fun relativeSizeToWidth(size: Int) = when {
        size <= 3 -> 200
        size in 4..7 -> 170
        else -> 140
    }

    fun selectIndicator(position: Int){
        for (index in lineView.indices){
            if (index == position) lineSelect(index)
            else lineDefault(index)
        }
    }

    private fun lineDefault(index: Int) = lineView[index].setBackgroundResource(mDefaultRes)
    private fun lineSelect(index: Int) = lineView[index].setBackgroundResource(mSelectRes)
}