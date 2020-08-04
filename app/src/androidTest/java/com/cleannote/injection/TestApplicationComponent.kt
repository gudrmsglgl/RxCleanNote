package com.cleannote.injection

import com.cleannote.NoteApplication
import com.cleannote.TestBaseApplication
import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.injection.module.*
import com.cleannote.ui.NoteListFragmentTest
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    TestCacheModule::class,
    TestDataModule::class,
    DomainModule::class,
    PresentationModule::class,
    TestRemoteModule::class,
    UiModule::class,
    TestApplicationModule::class
])
interface TestApplicationComponent: ApplicationComponent {

    fun provideNoteRepository(): NoteRepository

    fun providePostExecutionThread(): PostExecutionThread

    fun provideThreadExecutor(): ThreadExecutor

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance testApplication: TestBaseApplication): TestApplicationComponent
    }

    fun inject(noteListFragmentTest: NoteListFragmentTest)
}