package com.cleannote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.input
import com.cleannote.app.R
import com.cleannote.common.*
import com.cleannote.data.ui.InfoType
import com.cleannote.data.ui.InfoType.*
import com.cleannote.data.ui.InputType
import com.cleannote.data.ui.UIMessage
import com.cleannote.data.ui.UIType
import com.cleannote.extension.isVisible
import com.cleannote.extension.visible
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), UIController {

    @Inject
    lateinit var fragmentFactory: NoteFragmentFactory

    private var appBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        setFragmentFactory()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun inject(){
        (application as NoteApplication).applicationComponent.inject(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment)
            .navigateUp(appBarConfiguration as AppBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setFragmentFactory(){
        supportFragmentManager.fragmentFactory = fragmentFactory
    }

    override fun displayProgressBar(isProceed: Boolean) {
        if (isProceed) progress.visibility = VISIBLE
        else progress.visibility = GONE
    }

    override fun isDisplayProgressBar(): Boolean = progress.isVisible()

    override fun showUIMessage(
        uiMessage: UIMessage,
        dialogBtnCallback: DialogBtnCallback?,
        inputCaptureCallback: InputCaptureCallback?
    ) = when(uiMessage.uiType){
        UIType.Toast -> {
            showToast(uiMessage.message)
        }
        UIType.Dialog -> {
            makeDialog(uiMessage.message, uiMessage.infoType!!, dialogBtnCallback)
        }
        else -> {
            makeInputDialog(uiMessage.message, uiMessage.inputType!!, inputCaptureCallback)
        }
    }

    private fun makeDialog(message: String,
                           infoType: InfoType,
                           dialogBtnCallback: DialogBtnCallback?) {
        MaterialDialog(this).show {
            title(text = dialogTitle(infoType))
            message(text = message)
            dialogButton(infoType, dialogBtnCallback)
            cancelable(dialogIsCancelable(infoType))
        }
    }

    private fun dialogTitle(
        infoType: InfoType
    )= when (infoType){
        Confirm -> getString(R.string.dialog_title_confirm)
        Warning -> getString(R.string.dialog_title_warning)
        else -> getString(R.string.dialog_title_question)
    }

    private fun dialogIsCancelable(infoType: InfoType): Boolean{
        return infoType !is InfoType.Question
    }

    private fun MaterialDialog.dialogButton(
        infoType: InfoType,
        dialogBtnCallback: DialogBtnCallback?
    ) = when(infoType){
        Question -> {
            positiveButton(R.string.dialog_btn_yes) {
                dialogBtnCallback?.confirmProceed()
                dismiss()
            }
            negativeButton(R.string.dialog_btn_no) {
                dialogBtnCallback?.cancelProceed()
                dismiss()
            }
        }
        else -> {
            positiveButton(R.string.dialog_btn_confirm) {
                dialogBtnCallback?.confirmProceed()
                dismiss()
            }
        }
    }

    private fun makeInputDialog(
        message: String,
        inputType: InputType,
        inputCaptureCallback: InputCaptureCallback?
    ){
        MaterialDialog(this).show {
            var inputText: String? = null
            var isPositiveBtnClick = false
            message(text = message)
            input(hint = getInputHint(inputType)){ _, text ->
                inputText = text.toString()
            }
            positiveButton {
                inputCaptureCallback?.onTextCaptured(inputText!!)
                isPositiveBtnClick = true
            }
            onDismiss {
                if (!isPositiveBtnClick){
                    showToast(getString(R.string.dialog_login_input_dismiss))
                }
            }
        }
    }

    private fun getInputHint(inputType: InputType) = when(inputType){
        InputType.Login -> getString(R.string.dialog_id_hint)
        else -> getString(R.string.dialog_newnote_hint)
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        fragment
            ?.childFragmentManager
            ?.fragments
            ?.forEach {
                when (it){
                    is OnBackPressListener -> if (it.shouldBackPress()) super.onBackPressed()
                    else -> super.onBackPressed()
                }
            }
    }

}
