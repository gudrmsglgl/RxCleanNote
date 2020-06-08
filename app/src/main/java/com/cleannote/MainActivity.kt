package com.cleannote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cleannote.app.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun inject(){
        (application as NoteApplication).applicationComponent.inject(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }
}
