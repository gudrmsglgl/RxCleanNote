package com.cleannote.presentation.mapper

interface Mapper<out V, D> {
    fun mapToView(type: D): V
}