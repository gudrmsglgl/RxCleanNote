package com.cleannote.common

import com.cleannote.data.ui.UIMessage

interface UIController {
    fun displayProgressBar(isProceed: Boolean)
    fun showUIMessage(uiMessage: UIMessage,
                      dialogBtnCallback: DialogBtnCallback? = null,
                      inputCaptureCallback: InputCaptureCallback? = null)
}

interface DialogBtnCallback{
    fun confirmProceed()
    fun cancelProceed()
}

interface InputCaptureCallback{
    fun onTextCaptured(text: String)
}