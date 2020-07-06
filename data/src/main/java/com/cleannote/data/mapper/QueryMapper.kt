package com.cleannote.data.mapper

import com.cleannote.data.model.QueryEntity
import com.cleannote.domain.model.Query
import javax.inject.Inject

class QueryMapper @Inject constructor(): Mapper<QueryEntity, Query> {

    override fun mapFromEntity(type: QueryEntity): Query = Query(
        type.page, type.limit, type.sort, type.order, type.like
    )

    override fun mapToEntity(type: Query): QueryEntity = QueryEntity(
        type.page, type.limit, type.sort, type.order, type.like
    )
}