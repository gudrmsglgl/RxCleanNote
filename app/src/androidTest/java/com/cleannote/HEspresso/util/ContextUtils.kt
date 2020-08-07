package com.cleannote.HEspresso.util

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry

fun getResourceDrawable(@DrawableRes resId: Int) =
    ContextCompat.getDrawable(InstrumentationRegistry.getInstrumentation().targetContext, resId)

fun getResourceColor(@ColorRes resId: Int) =
    ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().targetContext, resId)
