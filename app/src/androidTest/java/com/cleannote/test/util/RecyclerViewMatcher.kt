package com.cleannote.test.util

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher constructor(@IdRes private var recyclerViewId: Int) {

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object: TypeSafeMatcher<View>() {
            internal var resource: Resources? = null
            internal var childView: View? = null

            override fun describeTo(description: Description?) {
                var idDescription = Integer.toString(recyclerViewId)
                if (this.resource != null){
                    try{

                    } catch (var4: Resources.NotFoundException){
                        idDescription = String.format("%s (resource name not found)",
                        *arrayOf<Any>(Integer.valueOf(recyclerViewId)))
                    }
                }
                description?.appendText("with id: " + idDescription)
            }

            override fun matchesSafely(view: View): Boolean {
                this.resource = view.resources

                if (childView == null){
                    val recyclerView =
                        view.rootView.findViewById<RecyclerView>(recyclerViewId) as RecyclerView
                    if (recyclerView != null && recyclerView.id == recyclerViewId) {
                        childView = recyclerView.findViewHolderForAdapterPosition(position)!!.itemView
                    } else {
                        return false
                    }
                }
                if (targetViewId == -1) {
                    return view === childView
                } else {
                    val targetView = childView?.findViewById<View>(targetViewId)
                    return view === targetView
                }
            }
        }
    }
}