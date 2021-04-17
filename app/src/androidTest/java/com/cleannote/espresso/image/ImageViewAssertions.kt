package com.cleannote.espresso.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.test.espresso.assertion.ViewAssertions
import com.cleannote.espresso.assertion.BaseAssertion
import com.cleannote.notedetail.view.GlideLoadState

interface ImageViewAssertions : BaseAssertion {

    fun hasDrawable(@DrawableRes resId: Int, toBitmap: ((drawable: Drawable) -> Bitmap)? = null) {
        viewInteraction.check(
            ViewAssertions.matches(
                DrawableMatcher(resId = resId, toBitmap = toBitmap)
            )
        )
    }

    fun hasDrawable(drawable: Drawable, toBitmap: ((drawable: Drawable) -> Bitmap)? = null) {
        viewInteraction.check(
            ViewAssertions.matches(
                DrawableMatcher(drawable = drawable, toBitmap = toBitmap)
            )
        )
    }

    fun glideLoadState(@IdRes key: Int, @GlideLoadState type: String) {
        viewInteraction.check { view, noViewFoundException ->
            if (view is ImageView) {
                val loadState: String = view.getTag(key) as String
                if (loadState != type) throw AssertionError(
                    "Expected state: $type" +
                        " but actual is $loadState"
                )
            } else {
                noViewFoundException?.let { throw AssertionError(it) }
            }
        }
    }
}
