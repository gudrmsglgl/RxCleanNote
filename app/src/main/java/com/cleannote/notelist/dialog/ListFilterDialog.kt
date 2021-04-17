package com.cleannote.notelist.dialog

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.cleannote.app.R
import com.cleannote.common.dialog.BaseDialog
import com.cleannote.domain.Constants
import com.cleannote.extension.rxbinding.singleClick
import com.jakewharton.rxbinding4.widget.checkedChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction

class ListFilterDialog(
    override val context: Context,
    private val sharedPref: SharedPreferences,
    private val viewLifeCycleOwner: LifecycleOwner
) : BaseDialog {

    override fun makeDefaultDialog(): MaterialDialog = MaterialDialog(context).show {
        customView(R.layout.layout_filter)
        cancelable(true)
    }

    fun showDialog(okBtnSource: (MaterialDialog, String) -> Unit) = makeDefaultDialog()
        .show {
            val view = getCustomView()
            val filterOk = view.findViewById<Button>(R.id.filter_btn_ok)

            val setOrderSource = dialogOkClickSource(dialogView = view, okBtn = filterOk)
                .subscribe {
                    okBtnSource.invoke(this, it)
                }
            lifecycleOwner(viewLifeCycleOwner)
            onDismiss { setOrderSource.dispose() }
        }

    private fun dialogOkClickSource(
        dialogView: View,
        okBtn: View
    ) = Observable.combineLatest(
        selectedRadioBtnSource(dialogView),
        okBtn.singleClick(),
        BiFunction { selectedRadioBtn: Int, _: Unit ->
            selectedRadioBtn
        }
    )
        .map {
            if (it == R.id.radio_btn_desc)
                Constants.ORDER_DESC
            else
                Constants.ORDER_ASC
        }

    private fun selectedRadioBtnSource(view: View) = initCheckedRadioGroup(view).checkedChanges()

    private fun initCheckedRadioGroup(view: View): RadioGroup {
        return view
            .findViewById<RadioGroup>(R.id.radio_group)
            .apply {
                check(getCachedRadioBtn())
            }
    }

    @IdRes
    private fun getCachedRadioBtn(): Int {
        return if (Constants.ORDER_DESC == sharedPref.getString(
                Constants.FILTER_ORDERING_KEY,
                Constants.ORDER_DESC
            )
        )
            R.id.radio_btn_desc
        else
            R.id.radio_btn_asc
    }
}
