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
import com.cleannote.data.ui.UIMessage
import com.cleannote.data.ui.UIType
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

    override fun showUIMessage(
        uiMessage: UIMessage,
        buttonCallback: ButtonCallback?
    ) = when(uiMessage.uiType){
        UIType.Toast -> {
            showToast(uiMessage.message)
        }
        UIType.Dialog -> {
            makeDialog(uiMessage.message, uiMessage.infoType, buttonCallback)
        }
        else -> {
            makeInputDialog(uiMessage.message, buttonCallback)
        }
    }

    private fun makeDialog(message: String,
                           infoType: InfoType,
                           buttonCallback: ButtonCallback?) {
        MaterialDialog(this).show {
            title(text = dialogTitle(infoType))
            message(text = message)
            dialogButton(infoType, buttonCallback)
            cancelable(dialogIsCancelable(infoType))
        }
    }

    private fun dialogTitle(
        infoType: InfoType
    )= when (infoType){
        Confirm -> getString(R.string.dialog_title_confirm)
        Warning -> getString(R.string.dialog_title_warning)
        Question -> getString(R.string.dialog_title_question)
        else -> ""
    }

    private fun dialogIsCancelable(infoType: InfoType): Boolean{
        return infoType !is Question
    }

    private fun MaterialDialog.dialogButton(
        infoType: InfoType,
        buttonCallback: ButtonCallback?
    ) = when(infoType){
        Question -> {
            positiveButton(R.string.dialog_btn_yes) {
                buttonCallback?.confirmProceed()
                dismiss()
            }
            negativeButton(R.string.dialog_btn_no) {
                buttonCallback?.cancelProceed()
                dismiss()
            }
        }
        else -> {
            positiveButton(R.string.dialog_btn_confirm) {
                buttonCallback?.confirmProceed()
                dismiss()
            }
        }
    }

    private fun makeInputDialog(
        message: String,
        buttonCallback: ButtonCallback?
    ){
        MaterialDialog(this).show {
            var inputText: String? = null
            var isPositiveBtnClick = false
            message(text = message)
            input(hint = getString(R.string.dialog_id_hint)){ _, text ->
                inputText = text.toString()
            }
            positiveButton {
                buttonCallback?.inputValueReceive(inputText!!)
                isPositiveBtnClick = true
            }
            onDismiss {
                if (!isPositiveBtnClick){
                    showToast(getString(R.string.dialog_login_input_dismiss))
                }
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

}
