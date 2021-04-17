package com.cleannote.domain.usecase

import com.cleannote.domain.BaseDomainTest
import com.cleannote.domain.interactor.usecases.notelist.NextPageExist
import com.cleannote.domain.model.Query
import com.cleannote.domain.test.factory.QueryFactory
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NextPageExistTest : BaseDomainTest<Single<Boolean>, Query>() {
    private lateinit var nextPageExist: NextPageExist
    private val query = QueryFactory.makeDefaultQuery()

    @BeforeEach
    fun setup() {
        nextPageExist = NextPageExist(repository, threadExecutor, postExecutionThread)
    }

    @Test
    fun buildUseCaseCallRepositoryFunc() {
        stubRepositoryReturnValue(query, Single.just(false))
        whenBuildUseCase(query)
            .test()
        verifyRepoNextPageExist(query)
    }

    @Test
    fun buildUseCaseRepoFuncReturnValue() {
        stubRepositoryReturnValue(query, Single.just(false))
        whenBuildUseCase(query)
            .test()
            .assertValue(false)
    }

    @Test
    fun buildUseCaseRxObservableComplete() {
        stubRepositoryReturnValue(query, Single.just(false))
        whenBuildUseCase(query)
            .test()
            .assertComplete()
    }

    override fun whenBuildUseCase(param: Query): Single<Boolean> {
        return nextPageExist.buildUseCaseSingle(param)
    }

    override fun stubRepositoryReturnValue(param: Query?, stubValue: Single<Boolean>) {
        whenever(repository.nextPageExist(param!!)).thenReturn(stubValue)
    }

    private fun verifyRepoNextPageExist(param: Query) {
        verify(repository).nextPageExist(param)
    }
}
