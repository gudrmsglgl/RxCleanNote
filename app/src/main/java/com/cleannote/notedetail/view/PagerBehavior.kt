package com.cleannote.notedetail.view

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.viewpager2.widget.ViewPager2

class PagerBehavior(
    context: Context?,
    attrs: AttributeSet?
) : CoordinatorLayout.Behavior<ViewPager2>(context, attrs) {

    private val INTERPOLATOR = FastOutSlowInInterpolator()
    private val ANIMATION_DURATION:Long = 200

    private var dyDirectionSum = 0
    var isShowing: Boolean = true
    var isHiding: Boolean = false

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ViewPager2,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ViewPager2,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        // 스크롤이 반대방향으로 전환
        if (dy > 0 && dyDirectionSum < 0 || dy < 0 && dyDirectionSum > 0){
            child.animate().cancel()
            dyDirectionSum = 0
        }

        dyDirectionSum += dy

        if (dyDirectionSum > child.height) {
            hideView(child)
        } else if (dyDirectionSum < -child.height){
            showView(child)
        }
    }

    private fun hideView(view: View) {
        if (isHiding || view.visibility != View.VISIBLE) {
            return
        }

        val animator = view.animate()
            .translationY(view.height.toFloat())
            .setInterpolator(INTERPOLATOR)
            .setDuration(ANIMATION_DURATION)

        animator.setListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
                isHiding = true
            }

            override fun onAnimationEnd(p0: Animator?) {
                isHiding = false
                view.visibility = INVISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {
                // 취소시 다시 보여줌
                isHiding = false
                showView(view)
            }

            override fun onAnimationRepeat(p0: Animator?) {
                // no-op
            }
        })
        animator.start()
    }

    private fun showView(view: View) {
        if (isShowing || view.visibility == View.VISIBLE) {
            return
        }

        val animator = view.animate()
            .translationY(0.toFloat())
            .setInterpolator(INTERPOLATOR)
            .setDuration(ANIMATION_DURATION)

        animator.setListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
                isShowing = true
                view.visibility = VISIBLE
            }

            override fun onAnimationEnd(p0: Animator?) {
                isShowing = false
            }

            override fun onAnimationCancel(p0: Animator?) {
                // 취소시 다시 숨김
                isShowing = false
                hideView(view)
            }

            override fun onAnimationRepeat(p0: Animator?) {
                // no-op
            }
        })
        animator.start()
    }

}