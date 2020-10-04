package com.cleannote.HEspresso.check

import android.view.View
import com.cleannote.HEspresso.view.NBaseView
import org.hamcrest.Matcher

class NCheckBox(matcher: Matcher<View>): NBaseView<NCheckBox>(matcher), CheckableAssertions