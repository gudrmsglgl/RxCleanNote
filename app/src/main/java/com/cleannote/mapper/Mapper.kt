package com.cleannote.mapper

interface Mapper<out U, in V> {
    fun mapToUiModel(type: V): U
}