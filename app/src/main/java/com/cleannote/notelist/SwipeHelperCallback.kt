package com.cleannote.notelist

import android.graphics.Canvas
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.util.rangeTo
import androidx.core.util.toClosedRange
import androidx.core.util.toRange
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.cleannote.app.R
import com.cleannote.extension.gone
import com.cleannote.extension.visible
import com.cleannote.notelist.holder.NoteViewHolder
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SwipeHelperCallback(private var clamp: Float) : ItemTouchHelper.Callback() {

    companion object{
        const val ESCAPE_VELOCITY = 10
        const val MARGIN_SWIPE = 100F
    }

    private var currentPosition: Int? = null
    private var previousPosition: Int?= null
    private var currentDx = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int{
        return makeMovementFlags(0, START or END)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

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
        val isClamped = getTag(viewHolder)

        //setTag(viewHolder, !isClamped && currentDx <= -clamp)
        setTag(viewHolder, currentDx <= -clamp)

        /*else{
            debug("RIGHT DIRECTION: getSwipeThreshold")
            setTag(viewHolder, !isClamped && currentDx >= clamp)
        }*/
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
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            val view = swipeView(viewHolder)
            val isClamped = getTag(viewHolder)
            val x = clampViewPositionHorizontal(viewHolder, dX, isClamped, isCurrentlyActive)

            currentDx = x
            visibleDeleteMenu(
                viewHolder,
                isCurrentDxLeft = currentDx < 0
            )

            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                x,
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
    ): Float{
        return if (isClamped){
            if (isCurrentlyActive){
                drawLeftDxAfterClamped(dX, clamp)
            } else {
                drawLeftDxToClamp(clamp)
            }
        } else {
            debug("dX: $dX, abs(dX)/clamp: ${abs(dX) / clamp}")
            val alpha = abs(dX) / clamp
            deleteMenuView(holder).apply {
                this.alpha = alpha
            }
            drawLeftDxInLimitOrInit(limit = -clamp, dx = dX)
        }
    }

    // isClapmed: true isCurrentlyActive: true
    // 고정된 상태이므로 dX 부분을 처음 clamp 만큼 추가해줘야함
    // dx -clamp
    private fun drawLeftDxAfterClamped(
        dX: Float,
        clampedDx: Float
    ) = drawLeftDx(dxUpToLimit(-MARGIN_SWIPE -clampedDx, dX - clampedDx))
    // isClamped: true -> dX: -(왼쪽)clamp 값 만큼 그림

    // isClamped: false -> dX: 계속 작어짐 (-적으로) max() -> 기준치를 넘으면 그 기준치가 최대임
    private fun dxUpToLimit(limit: Float, dX: Float) = max(limit, dX)

    // left 로 그려라
    private fun drawLeftDx(calculateLeftDx: Float) = min(calculateLeftDx, 0f)

    private fun drawLeftDxInLimitOrInit(limit: Float, dx: Float) = drawLeftDx(
        calculateLeftDx = dxUpToLimit(limit, dx)
    )

    private fun drawLeftDxToClamp(clamp: Float) = drawLeftDx(-clamp)

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float =
        defaultValue * ESCAPE_VELOCITY

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean){
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean{
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun removePreviousClamp(recyclerView: RecyclerView){
        if (currentPosition == previousPosition) return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            //swipeOnCancel(viewHolder)
            //resetHolderProperty(viewHolder)
            noteItemOnCancel(viewHolder)
            setTag(viewHolder, false)
            previousPosition = null
        }
    }

    fun cancelDeleteMenu(holder: RecyclerView.ViewHolder){
        swipeOnCancel(holder)
    }

    private fun swipeView(viewHolder: RecyclerView.ViewHolder) =
        (viewHolder as NoteViewHolder).binding.swipeView

    private fun deleteMenuView(viewHolder: RecyclerView.ViewHolder) =
        (viewHolder as NoteViewHolder).binding.swipeMenuDelete

    private fun visibleDeleteMenu(
        viewHolder: RecyclerView.ViewHolder,
        isCurrentDxLeft: Boolean
    ){
        if (isCurrentDxLeft)
            (viewHolder as NoteViewHolder).binding.swipeMenuDelete.visible()
        else
            (viewHolder as NoteViewHolder).binding.swipeMenuDelete.gone()
    }

    private fun swipeOnCancel(holder: RecyclerView.ViewHolder) = noteItemOnCancel(holder)
        .withStartAction {
            deleteMenuOnCancel(holder)
        }
        .withEndAction {
            resetHolderProperty(holder)
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

    private fun noteItemOnCancel(holder: RecyclerView.ViewHolder) = swipeView(holder)
        .animate()
        .setInterpolator(LinearInterpolator())
        .translationX(0f)
        .setDuration(300L)

    private fun resetHolderProperty(holder: RecyclerView.ViewHolder){
        swipeView(holder).translationX = 0f
        setTag(holder, false)
        previousPosition = null
    }

    private fun debug(msg: String) = Timber.tag("RxCleanNote").d(msg)
}