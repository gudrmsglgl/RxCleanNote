package com.cleannote.notelist

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class NoteItemTouchHelperCallback constructor(
    private val touchAdapter: TouchAdapter,
    private val background: ColorDrawable
): ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(
        ItemTouchHelper.ACTION_STATE_IDLE,
        ItemTouchHelper.START or ItemTouchHelper.END
    )

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return touchAdapter.isSwipeEnable()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        touchAdapter.onSwiped(viewHolder.bindingAdapterPosition)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        Timber.tag("RxCleanNote").d("NoteItemTouchCallback clearView()")
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        Timber.tag("RxCleanNote").d("NoteItemTouchCallback onChildDraw() dX:$dX , actionState: $actionState, isCurrentlyActive: $isCurrentlyActive")

        val itemView = viewHolder.itemView

        when {
            dX > 0 -> background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
            dX < 0 -> background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            else -> background.setBounds(0,0,0,0)
        }

        background.draw(c)

    }
}

interface TouchAdapter{
    fun onSwiped(position: Int)
    fun isSwipeEnable(): Boolean
}