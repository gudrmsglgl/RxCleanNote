package com.cleannote.espresso.viewpager

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NViewPagerItem(matcher: Matcher<View>): NBaseView<NViewPagerItem>(matcher)