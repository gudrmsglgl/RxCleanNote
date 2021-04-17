package com.cleannote.domain.test.factory

import com.cleannote.domain.model.User

object UserFactory {

    fun createUsers(num: Int) = (0 until num)
        .map { User("user#$it", "nick#$it") }
        .toList()
}
