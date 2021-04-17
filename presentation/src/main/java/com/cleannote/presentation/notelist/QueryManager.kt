package com.cleannote.presentation.notelist

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cleannote.domain.Constants.FILTER_ORDERING_KEY
import com.cleannote.domain.Constants.ORDER_DESC
import com.cleannote.domain.Constants.QUERY_DEFAULT_LIMIT
import com.cleannote.domain.Constants.QUERY_DEFAULT_PAGE
import com.cleannote.domain.Constants.SORT_UPDATED_AT
import com.cleannote.domain.interactor.usecases.notelist.NextPageExist
import com.cleannote.domain.model.Query
import com.cleannote.presentation.data.DataState
import javax.inject.Inject

class QueryManager
@Inject constructor(
    private val sharedPref: SharedPreferences,
    private val nextPageExist: NextPageExist
) {

    private val _query: MutableLiveData<Query> = MutableLiveData(
        Query(order = cacheOrdering())
    )
    val query: LiveData<Query>
        get() = _query

    private var _isNextPageExist: MutableLiveData<DataState<Boolean>> = MutableLiveData()
    val isNextPageExist: Boolean
        get() = _isNextPageExist.value?.data ?: false

    fun executeNextPageExist(query: Query) {
        _isNextPageExist.postValue(DataState.loading())
        nextPageExist.execute(
            onSuccess = {
                _isNextPageExist.postValue(DataState.success(it))
            },
            onError = {
                _isNextPageExist.postValue(DataState.error(it))
            },
            params = query
        )
    }

    fun nextPageQuery(): Query {
        val nextPage = getQuery().page + 1
        return getQuery().copy(page = nextPage)
    }

    fun nextPageQuery(nextPage: Int, startIndex: Int): Query {
        return getQuery().copy(
            page = nextPage, startIndex = startIndex
        )
    }

    fun resetSearchQuery(keyword: String) = updateQuery(
        param = getQuery().apply {
            page = QUERY_DEFAULT_PAGE
            limit = QUERY_DEFAULT_LIMIT
            sort = SORT_UPDATED_AT
            order = cacheOrdering()
            like = keyword
        },
        isBackGround = true
    )

    fun resortingByOrder(param: String) = updateQuery(
        getQuery().apply {
            page = QUERY_DEFAULT_PAGE
            order = param
            startIndex = null
        }
    )

    fun updateNextPage() = updateQuery(
        getQuery().apply { page += 1 }
    )

    fun resetPageWithIndex(
        targetPage: Int,
        index: Int
    ) = updateQuery(
        getQuery().apply {
            page = targetPage
            startIndex = index
        }
    )

    fun clearQuery() = updateQuery(
        getQuery()
            .apply {
                page = QUERY_DEFAULT_PAGE
                limit = QUERY_DEFAULT_LIMIT
                sort = SORT_UPDATED_AT
                order = cacheOrdering()
                like = null
                startIndex = null
            }
    )

    private fun updateQuery(param: Query, isBackGround: Boolean = false) {
        if (!isBackGround)
            _query.value = param
        else
            _query.postValue(param)
    }

    fun getQuery() = _query.value ?: Query(
        order = cacheOrdering()
    )

    fun queryLike() = _query.value?.like ?: ""

    fun cacheOrdering() = sharedPref
        .getString(FILTER_ORDERING_KEY, ORDER_DESC) ?: ORDER_DESC

    fun dispose() = nextPageExist.dispose()

    @VisibleForTesting
    fun isNextPageExist() = _isNextPageExist.value
}
