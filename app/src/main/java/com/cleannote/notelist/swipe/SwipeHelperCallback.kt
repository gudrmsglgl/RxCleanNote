package com.cleannote.notelist.swipe

import android.graphics.Canvas
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.cleannote.app.R
import com.cleannote.extension.gone
import com.cleannote.extension.visible
import com.cleannote.notelist.holder.NoteViewHolder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SwipeHelperCallback(
    private var clamp: Float,
    private val extendClamp: Float
) : ItemTouchHelper.Callback() {

    companion object {
        const val ESCAPE_VELOCITY = 10
    }

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f

    private lateinit var swipeAdapter: SwipeAdapter

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(0, START or END)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { Unit }

    override fun isItemViewSwipeEnabled(): Boolean {
        return swipeAdapter.isSwipeEnabled()
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        previousPosition = viewHolder.bindingAdapterPosition
        getDefaultUIUtil().clearView(swipeView(viewHolder))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentPosition = viewHolder.bindingAdapterPosition
            getDefaultUIUtil().onSelected(swipeView(it))
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        setClamp(viewHolder, isClamped = currentDx <= -clamp)
        return 2f
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
        if (actionState == ACTION_STATE_SWIPE) {
            val view = swipeView(viewHolder)
            val drawDx = clampViewPositionHorizontal(viewHolder, dX, isClamp(viewHolder), isCurrentlyActive)

            currentDx = drawDx
            visibleDeleteMenu(viewHolder, setVisible = currentDx < 0)

            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                drawDx,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }

    private fun clampViewPositionHorizontal(
        holder: RecyclerView.ViewHolder,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ): Float {
        return if (isClamped) {
            if (isCurrentlyActive) {
                drawLeftDxAfterClamped(dX, clamp)
            } else {
                drawLeftDxToClamp(clamp)
            }
        } else {
            deleteMenuAlpha(holder, dX)
            drawLeftDxInLimitOrInit(limit = -clamp, dx = dX)
        }
    }

    private fun drawLeftDxAfterClamped(
        dX: Float,
        clampedDx: Float
    ) = drawLeftDx(
        dxUpToLimit(-extendClamp - clampedDx, dX - clampedDx)
    )

    private fun dxUpToLimit(limit: Float, dX: Float) = max(limit, dX)

    private fun drawLeftDx(calculateLeftDx: Float) = min(calculateLeftDx, 0f)

    private fun drawLeftDxInLimitOrInit(limit: Float, dx: Float) = drawLeftDx(
        calculateLeftDx = dxUpToLimit(limit, dx)
    )

    private fun drawLeftDxToClamp(clamp: Float) = drawLeftDx(-clamp)

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float =
        defaultValue * ESCAPE_VELOCITY

    private fun setClamp(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    private fun isClamp(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition) return
        else {
            previousDeleteMenuClose(recyclerView)
        }
    }

    fun previousDeleteMenuClose(
        recyclerView: RecyclerView
    ): Boolean {
        return previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return false
            closeDeleteMenu(viewHolder)
            true
        } ?: false
    }

    fun isVisibleDeleteMenu(): Boolean = previousPosition?.let {
        true
    } ?: false

    private fun closeDeleteMenu(holder: RecyclerView.ViewHolder) {
        swipeRight(holder)
            .withStartAction {
                deleteMenuOnCancel(holder)
            }
            .withEndAction {
                visibleDeleteMenu(holder, setVisible = false)
            }
        releaseClamp(holder)
    }

    private fun swipeView(viewHolder: RecyclerView.ViewHolder) =
        (viewHolder as NoteViewHolder).binding.swipeView

    private fun deleteMenuView(viewHolder: RecyclerView.ViewHolder) =
        (viewHolder as NoteViewHolder).binding.swipeMenuDelete

    private fun visibleDeleteMenu(
        viewHolder: RecyclerView.ViewHolder,
        setVisible: Boolean
    ) {
        if (setVisible)
            (viewHolder as NoteViewHolder).binding.swipeMenuDelete.visible()
        else
            (viewHolder as NoteViewHolder).binding.swipeMenuDelete.gone()
    }

    private fun deleteImageAnimator(holder: RecyclerView.ViewHolder) = deleteMenuView(holder)
        .findViewById<ImageView>(R.id.swipe_delete_img)
        .animate()

    private fun deleteMenuOnCancel(holder: RecyclerView.ViewHolder) = deleteImageAnimator(holder)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .translationX(100f)
        .setDuration(300L)
        .withEndAction {
            deleteImageAnimator(holder)
                .translationXBy(-100f)
        }

    private fun deleteMenuAlpha(
        holder: RecyclerView.ViewHolder,
        dX: Float
    ) {
        deleteMenuView(holder)
            .apply {
                this.alpha = abs(dX) / clamp
            }
    }

    private fun swipeRight(holder: RecyclerView.ViewHolder) = swipeView(holder)
        .animate()
        .setInterpolator(LinearInterpolator())
        .translationX(0f)
        .setDuration(300L)

    private fun releaseClamp(holder: RecyclerView.ViewHolder) {
        setClamp(holder, false)
        previousPosition = null
    }

    fun setSwipeAdapter(adapter: SwipeAdapter) {
        swipeAdapter = adapter
    }
}
