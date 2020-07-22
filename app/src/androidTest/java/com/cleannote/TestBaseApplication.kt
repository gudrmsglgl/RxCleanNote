package com.cleannote

import com.cleannote.injection.DaggerTestApplicationComponent

class TestBaseApplication: NoteApplication() {
    override fun initApplicationComponent() {
        applicationComponent = DaggerTestApplicationComponent.factory().create(this)
    }
}