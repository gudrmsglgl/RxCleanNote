package com.cleannote.HEspresso.actions

interface ScrollableActions: BaseActions {
    fun scrollToStart()
    fun scrollToEnd()
    fun scrollTo(position: Int)
}