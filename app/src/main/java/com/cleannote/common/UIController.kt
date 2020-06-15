package com.cleannote.common

import com.cleannote.data.ui.UIMessage

interface UIController {
    fun displayProgressBar(isProceed: Boolean)
    fun showUIMessage(uiMessage: UIMessage, buttonCallback: ButtonCallback? = null)
}

interface ButtonCallback{
    fun confirmProceed()
    fun cancelProceed()
    fun inputValueReceive(value: String)
}