package com.cleannote.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.cleannote.app.R
import com.cleannote.data.ui.InfoType
import com.cleannote.data.ui.InputType
import com.cleannote.data.ui.UIMessage
import com.cleannote.data.ui.UIType
import com.cleannote.presentation.data.DataState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.rxbinding4.view.clicks
import com.wada811.databinding.dataBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseFragment<DataBinding : ViewDataBinding>(@LayoutRes layoutRes: Int): Fragment(
    layoutRes
) {

    lateinit var uiController: UIController

    private var disposables: CompositeDisposable? = null

    val binding: DataBinding by dataBinding()

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

    fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        activity?.let {
            it.windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }
        return 0
    }

    fun dimenPx(@DimenRes res: Int): Int{
        return context?.resources?.getDimensionPixelSize(res) ?: 0
    }

    fun showToast(message: String) = activity?.let {
        Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
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
                timber("d", "$context must implement com.cleannote.common.UIController")
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
        var isNullDrawable = false
        val loadDrawableBitmap  = ContextCompat.getDrawable(it, drawable)?.toBitmap()
        if (loadDrawableBitmap == null || this == null){
            isNullDrawable = true
            return isNullDrawable
        }
        else
            this.toBitmap().sameAs(loadDrawableBitmap)
    }?: false

    fun <T> DataState<T>.sendFirebaseThrowable(){
        this.throwable?.let { FirebaseCrashlytics.getInstance().recordException(it) }
    }

    fun setStatusBarColor(@ColorRes color: Int){
        activity?.window?.statusBarColor = getColor(color)
    }

    private fun getColor(@ColorRes color: Int): Int = ContextCompat.getColor(requireContext(), color)

    fun setStatusBarTextBlack(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun setStatusBarTextTrans(){
        val decorView: View = activity?.window?.decorView!! //set status background black

        decorView.systemUiVisibility =
            decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //set status text  light
    }

    fun <T: ViewDataBinding> bindingInflate(@LayoutRes layoutRes: Int, parent: ViewGroup): T {
        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutRes, parent, false)
    }

    abstract fun initBinding()
}