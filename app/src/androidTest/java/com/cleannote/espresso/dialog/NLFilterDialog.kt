package com.cleannote.espresso.dialog

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import com.cleannote.espresso.button.NButton
import com.cleannote.espresso.text.NTextView
import com.cleannote.espresso.view.NBaseView
import com.cleannote.app.R
import org.hamcrest.Matcher

class NLFilterDialog(private val viewMatcher: Matcher<View>):
    NBaseView<NLFilterDialog>(viewMatcher)
{
    val mainTitle = NTextView(ViewMatchers.withId(R.id.filter_option_title))
    val subTitle = NTextView(ViewMatchers.withId(R.id.filter_option_desc))
    val radioBtnDesc =
        NButton(ViewMatchers.withText(R.string.radio_btn_desc))
    val radioBtnAsc =
        NButton(ViewMatchers.withText(R.string.radio_btn_asc))
    val sortBtn =
        NButton(ViewMatchers.withId(R.id.filter_btn_ok))
}