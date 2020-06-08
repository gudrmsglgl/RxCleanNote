package com.cleannote.presentation.util

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil @Inject constructor() {

    fun getCurrentTimestamp(): String =
        SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.KOREA).format(Date())

}