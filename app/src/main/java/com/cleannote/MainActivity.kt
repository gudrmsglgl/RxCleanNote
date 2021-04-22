package com.cleannote

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.cleannote.app.R
import com.cleannote.app.databinding.ActivityMainBinding
import com.cleannote.common.NoteFragmentFactory
import com.cleannote.common.OnBackPressListener
import com.cleannote.common.UIController
import com.cleannote.extension.isVisible
import com.cleannote.notedetail.edit.NoteDetailEditFragment
import com.cleannote.notedetail.view.NoteDetailViewFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class MainActivity : AppCompatActivity(), UIController {

    @Inject
    lateinit var fragmentFactory: NoteFragmentFactory

    private var appBarConfiguration: AppBarConfiguration? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        setFragmentFactory()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics = Firebase.analytics
    }

    private fun inject() {
        (application as NoteApplication).applicationComponent.inject(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment)
            .navigateUp(appBarConfiguration as AppBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setFragmentFactory() {
        supportFragmentManager.fragmentFactory = fragmentFactory
    }

    override fun displayProgressBar(isProceed: Boolean) = with(binding) {
        if (isProceed) progress.visibility = VISIBLE
        else progress.visibility = GONE
    }

    override fun isDisplayProgressBar(): Boolean = binding.progress.isVisible()

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHostFragment
            ?.childFragmentManager
            ?.fragments
            ?.forEach { fragment ->
                when (fragment) {
                    is OnBackPressListener -> if (fragment.shouldBackPress()) super.onBackPressed()
                    is NoteDetailViewFragment -> fragment.navPopBackStack()
                    is NoteDetailEditFragment -> fragment.navPopBackStack()
                    else -> super.onBackPressed()
                }
            }
    }
}
