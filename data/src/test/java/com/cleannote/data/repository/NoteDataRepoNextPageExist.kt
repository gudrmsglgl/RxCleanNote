package com.cleannote.data.repository

import com.cleannote.data.BaseNoteRepositoryTest
import com.cleannote.data.extensions.transQueryEntity
import com.cleannote.data.test.factory.QueryFactory
import com.cleannote.domain.model.Query
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NoteDataRepoNextPageExist: BaseNoteRepositoryTest() {
    private val query = QueryFactory.makeQuery(page = 2)

    @Test
    @DisplayName("TestCase[Cache]: Call Cache DataStore Func")
    fun testCase_callCacheDataStore(){
        stubContainer {
            cDataStoreStubber {
                nextPageExist(
                    param = query.transQueryEntity(),
                    stub = true
                )
            }
        }
        whenRepoNextPageExist(query)
            .test()

        dataStoreVerifyScope {
            nextPageExist(query.transQueryEntity())
        }
    }

    @Test
    @DisplayName("TestCase[Cache]: AssertComplete")
    fun testCase_assertComplete(){
        stubContainer {
            cDataStoreStubber {
                nextPageExist(
                    param = query.transQueryEntity(),
                    stub = true
                )
            }
        }
        whenRepoNextPageExist(query)
            .test()
            .assertComplete()
    }

    @Test
    @DisplayName("TestCase[Cache]: AssertValue -> Boolean")
    fun testCase_assertValue(){
        stubContainer {
            cDataStoreStubber {
                nextPageExist(
                    param = query.transQueryEntity(),
                    stub = true
                )
            }
        }
        whenRepoNextPageExist(query)
            .test()
            .assertValue(true)
    }


    private fun whenRepoNextPageExist(param: Query) = noteDataRepository.nextPageExist(param)
}