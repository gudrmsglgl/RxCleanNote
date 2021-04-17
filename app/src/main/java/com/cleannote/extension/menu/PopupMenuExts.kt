package com.cleannote.extension.menu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import com.cleannote.app.R
import com.cleannote.notedetail.edit.PickerType
import com.cleannote.notedetail.edit.PickerType.Companion.GALLERY
import com.jakewharton.rxbinding4.widget.itemClicks

inline fun FragmentActivity.showImageLoaderMenu(
    anchor: View,
    crossinline receiver: (Int) -> Unit
) {
    PopupMenu(this, anchor).apply {
        menuInflater.inflate(R.menu.menu_image_add, menu)
        visibleIcon(this@showImageLoaderMenu)
        val disposable = itemClicks()
            .map { menuItem ->
                when (menuItem.itemId) {
                    R.id.album -> GALLERY
                    R.id.camera -> PickerType.CAMERA
                    else -> PickerType.LINK
                }
            }
            .subscribe {
                receiver.invoke(it)
            }
        setOnDismissListener {
            disposable.dispose()
        }
        show()
    }
}

fun PopupMenu.visibleIcon(context: Context) {
    val menu = this.menu
    if (hasIcon(menu)) {
        for (i in 0 until menu.size()) {
            insertMenuItemIcon(context, menu.getItem(i))
        }
    }
}

/**
 * @return true if the menu has at least one MenuItem with an icon.
 */
private fun hasIcon(menu: Menu): Boolean {
    for (i in 0 until menu.size()) {
        if (menu.getItem(i).icon != null) return true
    }
    return false
}

/**
 * Converts the given MenuItem's title into a Spannable containing both its icon and title.
 */
private fun insertMenuItemIcon(context: Context, menuItem: MenuItem) {
    var icon: Drawable = menuItem.icon

    // If there's no icon, we insert a transparent one to keep the title aligned with the items
    // which do have icons.
    if (icon == null) icon = ColorDrawable(Color.TRANSPARENT)

    val iconSize = context.resources.getDimensionPixelSize(R.dimen.menu_item_icon_size)
    icon.setBounds(0, 0, iconSize, iconSize)
    val imageSpan = ImageSpan(icon)

    // Add a space placeholder for the icon, before the title.
    val ssb = SpannableStringBuilder("       " + menuItem.title)

    // Replace the space placeholder with the icon.
    ssb.setSpan(imageSpan, 1, 2, 0)
    menuItem.title = ssb
    // Set the icon to null just in case, on some weird devices, they've customized Android to display
    // the icon in the menu... we don't want two icons to appear.
    menuItem.icon = null
}
