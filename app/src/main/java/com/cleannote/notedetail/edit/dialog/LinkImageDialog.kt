package com.cleannote.notedetail.edit.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cleannote.app.R
import com.cleannote.common.dialog.BaseDialog
import com.cleannote.extension.changeTextColor
import com.cleannote.extension.gone
import com.cleannote.extension.rxbinding.singleClick
import com.cleannote.extension.visible
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class LinkImageDialog(
    override val context: Context,
    private val glideRequestManager: RequestManager,
    private val lifeCycleOwner: LifecycleOwner
) : BaseDialog {

    private lateinit var linkCustomView: View
    private val pathSubject: PublishSubject<String> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()

    override fun makeDefaultDialog(): MaterialDialog = MaterialDialog(context).show {
        customView(R.layout.layout_link_input)
        linkCustomView = getCustomView()
        val ivLink: ImageView = linkCustomView.findViewById(R.id.iv_loaded)
        val confirmBtn: TextView = linkCustomView.findViewById(R.id.tv_confirm)

        confirmBtnChangeActive(pathSubject, confirmBtn)
            .addCompositeDisposable()

        linkLoadSource(view = linkCustomView, preview = ivLink, receiveSubject = pathSubject)
            .addCompositeDisposable()

        cancelClick(linkCustomView)
            .subscribe {
                showToast(R.string.cancel_message)
                dismiss()
            }
            .addCompositeDisposable()

        lifecycleOwner(lifeCycleOwner)

        onDismiss {
            pathSubject.onComplete()
            compositeDisposable.dispose()
        }
    }

    fun onUploadImage(func: (String) -> Unit) = makeDefaultDialog()
        .uploadImageSource(linkCustomView, pathSubject, func)

    private fun confirmBtnChangeActive(
        pathSubject: PublishSubject<String>,
        btn: TextView
    ) = pathSubject
        .subscribe {
            if (it.isNotEmpty())
                btn.activeOn()
            else
                btn.activeOff()
        }

    private fun linkLoadSource(
        view: View,
        preview: ImageView,
        receiveSubject: PublishSubject<String>
    ) = inputLinkTextSource(view)
        .flatMapSingle {
            glideLoadImageSource(path = it.toString(), preview = preview)
        }
        .subscribe {
            val isResourceReady = it.first
            val path = it.second
            receiveSubject.onNext(
                if (isResourceReady) path else ""
            )
        }

    private fun MaterialDialog.uploadImageSource(
        view: View,
        pathSubject: PublishSubject<String>,
        func: (String) -> Unit
    ) = Observable.combineLatest(
        confirmClick(view),
        pathSubject,
        BiFunction { _: Unit, path: String ->
            path
        }
    ).subscribe {
        func.invoke(it)
        dismiss()
    }.addCompositeDisposable()

    private fun inputLinkTextSource(
        view: View
    ) = view
        .findViewById<EditText>(R.id.edit_link)
        .textChanges()
        .skipInitialValue()
        .debounce(1000L, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())

    private fun glideLoadImageSource(
        path: String,
        preview: ImageView
    ) = Single.create<Pair<Boolean, String>> {
        glideRequestManager
            .asBitmap()
            .load(path)
            .into(object : CustomTarget<Bitmap>() {

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    it.onSuccess(false to path)
                    emptyPathPreViewGone(path, preview)
                    preview.setImageDrawable(errorDrawable)
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    it.onSuccess(true to path)
                    preview.visible()
                    preview.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    preview.setImageDrawable(placeholder)
                }
            })
    }

    private fun confirmClick(view: View) = view
        .findViewById<TextView>(R.id.tv_confirm)
        .singleClick()

    private fun cancelClick(view: View) = view
        .findViewById<TextView>(R.id.tv_cancel)
        .singleClick()

    private fun emptyPathPreViewGone(path: String, view: ImageView) =
        if (path.isEmpty()) view.gone()
        else view.visible()

    private fun TextView.activeOn() {
        this.changeTextColor(R.color.green)
        this.isEnabled = true
    }

    private fun TextView.activeOff() {
        this.changeTextColor(R.color.default_grey)
        this.isEnabled = false
    }

    private fun Disposable.addCompositeDisposable() {
        compositeDisposable.add(this)
    }
}
