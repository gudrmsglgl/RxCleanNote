package com.cleannote.espresso.check

import android.view.View
import com.cleannote.espresso.view.NBaseView
import org.hamcrest.Matcher

class NCheckBox(matcher: Matcher<View>) : NBaseView<NCheckBox>(matcher), CheckableAssertions
