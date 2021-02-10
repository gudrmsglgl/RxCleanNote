package com.cleannote.extension.toolbar

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.MenuItem
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import com.cleannote.extension.resolveColor

inline fun Toolbar.setToolbar(
    @DrawableRes homeIcon: Int,
    @MenuRes menuRes: Int,
    crossinline onHomeClick: () -> Unit ,
    crossinline onMenuClick: (MenuItem) -> Boolean
){
    setNavigationIcon(homeIcon)
    inflateMenu(menuRes)
    setNavigationOnClickListener {
        onHomeClick.invoke()
    }
    setOnMenuItemClickListener {
        onMenuClick.invoke(it)
    }
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

fun Toolbar.setUI(
    titleParam: String?,
    @ColorRes iconColor: Int,
    backgroundColor: Int
) = apply {
    this.title = titleParam ?: ""
    setMenuIconColor(iconColor)
    setBackgroundColor(backgroundColor)
}