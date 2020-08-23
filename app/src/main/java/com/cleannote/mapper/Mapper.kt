package com.cleannote.mapper

interface Mapper<U,V> {
    fun mapToUiModel(type: V): U
    fun mapToView(type: U): V
}