package com.cleannote.espresso.actions

interface ScrollableActions: BaseActions {
    fun scrollToStart()
    fun scrollToEnd()
    fun scrollTo(position: Int)
}