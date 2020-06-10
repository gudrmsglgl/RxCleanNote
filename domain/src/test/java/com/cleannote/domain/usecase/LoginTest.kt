package com.cleannote.domain.usecase

import com.cleannote.domain.interactor.executor.PostExecutionThread
import com.cleannote.domain.interactor.executor.ThreadExecutor
import com.cleannote.domain.interactor.repository.NoteRepository
import com.cleannote.domain.interactor.usecases.splash.Login
import com.cleannote.domain.model.User
import com.cleannote.domain.test.factory.UserFactory
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Flowable
import io.reactivex.Maybe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginTest {

    private lateinit var mockThreadExecutor: ThreadExecutor
    private lateinit var mockPostExecutionThread: PostExecutionThread
    private lateinit var repository: NoteRepository

    private lateinit var login: Login
    private val successRetData = UserFactory.createUsers(1)
    private val failRetData: Flowable<List<User>> = Flowable.empty()

    @BeforeEach
    fun init(){
        mockThreadExecutor = mock()
        mockPostExecutionThread = mock()
        repository = mock{
            on { login("failId") } doReturn failRetData
            on { login("successId") } doReturn Flowable.just(successRetData)
        }
        login = Login(repository, mockThreadExecutor, mockPostExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepository(){
        login.buildUseCaseFlowable("successId")
        verify(repository).login("successId")
    }

    @Test
    fun buildUseCaseComplete(){
        val testObserver = login.buildUseCaseFlowable("successId").test()
        testObserver.assertComplete()
    }

    @Test
    fun buildUseCaseReturnSuccess(){
        val testObserver = login.buildUseCaseFlowable("successId").test()
        testObserver.assertValue(successRetData)
    }

    @Test
    fun buildUseCaseReturnEmpty(){
        val testObserver = login.buildUseCaseFlowable("failId").test()
        testObserver.assertNoValues()
    }

}