package com.cleannote.espresso.recycler.edit

import androidx.annotation.IdRes
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.cleannote.espresso.recycler.RecyclerActions
import com.cleannote.espresso.recycler.RecyclerAdapterAssertion
import com.cleannote.espresso.view.NBaseView

class NImgRecyclerView(@IdRes val rcvRes: Int): NBaseView<NImgRecyclerView>(withId(rcvRes)),
    RecyclerAdapterAssertion, RecyclerActions
{
    inline fun childAt(position: Int, function: NImgRecyclerItem.() -> Unit){
        function(NImgRecyclerItem(rcvRes, position))
    }

    inline fun firstItem(function: NImgRecyclerItem.() -> Unit){
        childAt(0, function)
    }
}