package com.cleannote.HEspresso.edit

import android.widget.EditText
import androidx.annotation.StringRes
import com.cleannote.HEspresso.assertion.BaseAssertion
import com.cleannote.HEspresso.util.getResourceString

interface EditAssertions: BaseAssertion {
    fun hasHint(hint: String) {
        viewInteraction.check { view, noViewFoundException ->
            if (view is EditText) {
                if (hint != view.hint) throw AssertionError("Expected hint is $hint," +
                        " but actual is ${view.hint}")
            } else {
                noViewFoundException?.let { throw AssertionError(it) }
            }
        }
    }

    fun hasHint(@StringRes resId: Int) {
        hasHint(getResourceString(resId))
    }

}