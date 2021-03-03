package com.cleannote.notedetail.view

import androidx.annotation.StringDef
import com.cleannote.notedetail.view.GlideLoadState.Companion.STATE_FAIL
import com.cleannote.notedetail.view.GlideLoadState.Companion.STATE_SUCCESS

@StringDef(STATE_SUCCESS, STATE_FAIL)
@Retention(AnnotationRetention.SOURCE)
annotation class GlideLoadState {
    companion object {
        const val STATE_SUCCESS = "Glide Load State Success"
        const val STATE_FAIL = "Glide Load State Fail"
    }
}
