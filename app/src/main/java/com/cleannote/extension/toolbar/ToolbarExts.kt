package com.cleannote.extension.toolbar

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import com.cleannote.extension.resolveColor

fun Toolbar.setToolbar(
    @DrawableRes homeIcon: Int,
    @MenuRes menuRes: Int,
    onHomeClickListener: View.OnClickListener,
    onMenuItemClickListener: Toolbar.OnMenuItemClickListener
){
    setNavigationIcon(homeIcon)
    inflateMenu(menuRes)
    setNavigationOnClickListener(onHomeClickListener)
    setOnMenuItemClickListener(onMenuItemClickListener)
}

fun Toolbar.setMenuIconColor(
    @ColorRes res: Int
) = apply {
    navigationIcon?.colorFilter =
        PorterDuffColorFilter(resolveColor(context, res), PorterDuff.Mode.MULTIPLY)
    menu.forEach {
        it.icon.colorFilter = PorterDuffColorFilter(resolveColor(context, res), PorterDuff.Mode.MULTIPLY)
    }
}
