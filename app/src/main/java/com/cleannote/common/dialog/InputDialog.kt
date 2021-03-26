package com.cleannote.common.dialog

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.cleannote.app.R

class InputDialog(
    override val context: Context,
    private val viewLifeCycleOwner: LifecycleOwner
): BaseDialog {

    private var hint: String? = null
    private var message: String? = null
    private var inputText: String? = null

    fun setHint(hint: String): InputDialog{
        this.hint = hint
        return this
    }

    fun setMessage(message: String): InputDialog {
        this.message = message
        return this
    }

    override fun makeDefaultDialog(): MaterialDialog = MaterialDialog(context)
        .show {
            message(text = message)
            input(hint = hint){ _ , charSequence ->
                inputText = charSequence.toString()
            }
            negativeButton {
                showToast(R.string.dialog_input_cancel)
                dismiss()
            }
            lifecycleOwner(viewLifeCycleOwner)
        }

    fun onPositiveClick(func: (String?) -> Unit) = makeDefaultDialog()
        .positiveButton {
            func.invoke(inputText)
        }

}