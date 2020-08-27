package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.splash.Login
import com.cleannote.domain.model.User
import com.cleannote.domain.test.factory.UserFactory
import com.cleannote.domain.usecase.common.FlowableUseCaseBuilder
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Flowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginTest: BaseDomainTest<String>(), FlowableUseCaseBuilder<List<User>, String> {

    private lateinit var login: Login

    private val successUserParam = "successId"
    private val successRetData = UserFactory.createUsers(1)

    private val failUserParam = "failId"
    private val failRetData: Flowable<List<User>> = Flowable.empty()

    @BeforeEach
    fun init(){
        mockRxSchedulers()
        repository = mock{
            on { login(failUserParam) } doReturn failRetData
            on { login(successUserParam) } doReturn Flowable.just(successRetData)
        }
        login = Login(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepository(){
        whenBuildUseCase(successUserParam)
        verifyRepositoryCall(successUserParam)
    }

    @Test
    fun buildUseCaseComplete(){
        val testObserver = whenBuildUseCase(successUserParam).test()
        testObserver.assertComplete()
    }

    @Test
    fun buildUseCaseReturnSuccess(){
        val testObserver =  whenBuildUseCase(successUserParam).test()
        testObserver.assertValue(successRetData)
    }

    @Test
    fun buildUseCaseReturnEmpty(){
        val testObserver =  whenBuildUseCase(failUserParam).test()
        testObserver.assertNoValues()
    }

    override fun whenBuildUseCase(param: String): Flowable<List<User>> {
        return repository.login(param)
    }

    override fun verifyRepositoryCall(param: String?) {
        verify(repository).login(param!!)
    }
}