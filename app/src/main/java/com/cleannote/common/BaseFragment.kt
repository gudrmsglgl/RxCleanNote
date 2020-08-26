package com.cleannote.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.cleannote.data.ui.InfoType
import com.cleannote.data.ui.InputType
import com.cleannote.data.ui.UIMessage
import com.cleannote.data.ui.UIType
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseFragment(@LayoutRes layoutRes: Int): Fragment(layoutRes) {

    lateinit var uiController: UIController

    private var disposables: CompositeDisposable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposables = CompositeDisposable()
        hideProgressBar()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setUIController(mockController = null)
    }

    fun timber(type: String, message: String){
        Timber.tag("RxCleanNote")
        when (type) {
            "d" -> Timber.d(message)
            "i" -> Timber.i(message)
            "e" -> Timber.e(message)
        }
    }

    fun showLoadingProgressBar(show: Boolean) = with(uiController){
        displayProgressBar(show)
    }

    fun showToast(message: String) = with (uiController){
        showUIMessage(
            UIMessage(message, UIType.Toast)
        )
    }

    fun showErrorMessage(
        message: String,
        dialogBtnCallback: DialogBtnCallback? = null
    ) = with(uiController) {
        showUIMessage(
            UIMessage(message, UIType.Dialog, InfoType.Warning),
            dialogBtnCallback
        )
    }

    fun showConfirmMessage(
        message: String,
        dialogBtnCallback: DialogBtnCallback? = null
    ) = with(uiController){
        showUIMessage(
            UIMessage(message, UIType.Dialog, InfoType.Confirm),
            dialogBtnCallback
        )
    }

    fun showInputDialog(
        message: String,
        inputType: InputType,
        inputCaptureCallback: InputCaptureCallback? = null
    ) = with(uiController){
        showUIMessage(
            UIMessage(message, UIType.Input, null, inputType),
            null,
            inputCaptureCallback
        )
    }

    fun setUIController(mockController: UIController?){
        if (mockController != null) this.uiController = mockController
        else
            try{
                uiController = context as UIController
            }catch (e: ClassCastException) {
                timber("d","$context must implement com.cleannote.common.UIController")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables?.clear()
        disposables?.dispose()
    }

    private fun hideProgressBar() = with(uiController){
        if (isDisplayProgressBar())
            displayProgressBar(false)
    }

    fun View.singleClick() =
        clicks().throttleFirst(2000, TimeUnit.MILLISECONDS)

    fun Disposable.addCompositeDisposable(){
        disposables?.add(this)
    }

    fun Drawable?.equalDrawable(@DrawableRes drawable: Int): Boolean = activity?.let{
        val loadDrawableBitmap  = ContextCompat.getDrawable(it, drawable)?.toBitmap()
        if (loadDrawableBitmap == null || this == null)
            false
        else
            this.toBitmap().sameAs(loadDrawableBitmap)
    }?: false

    fun getDrawable(@DrawableRes drawable: Int) = activity?.let {
        ContextCompat.getDrawable(it.applicationContext, drawable)
    }
}