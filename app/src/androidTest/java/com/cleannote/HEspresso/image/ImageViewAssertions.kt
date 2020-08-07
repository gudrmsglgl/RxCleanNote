package com.cleannote.HEspresso.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.test.espresso.assertion.ViewAssertions
import com.cleannote.HEspresso.assertion.BaseAssertion

interface ImageViewAssertions: BaseAssertion {

    fun hasDrawable(@DrawableRes resId: Int, toBitmap: ((drawable: Drawable) -> Bitmap)? = null){
        viewInteraction.check(ViewAssertions.matches(
            DrawableMatcher(resId = resId, toBitmap = toBitmap)
        ))
    }

    fun hasDrawable(drawable: Drawable, toBitmap: ((drawable: Drawable) -> Bitmap)? = null){
        viewInteraction.check(ViewAssertions.matches(
            DrawableMatcher(drawable = drawable, toBitmap = toBitmap)
        ))
    }

}